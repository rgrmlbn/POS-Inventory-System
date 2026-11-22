# Product and Category Services: Q&A Notes

This beginner guide explains the product image flow in the frontend
`ProductService.ts` and how it differs from the category service.

## Q1. What is the difference between POST and PATCH?

**POST** creates a new product. **PATCH** changes an existing product.

|  | POST | PATCH |
| --- | --- | --- |
| Purpose | Create something new | Edit an existing product |
| Needs an ID? | No; the server creates one | Yes; the URL includes the product ID |
| Fields | Required product fields | Only the fields being changed |

The form submits an image as a browser `File`. The backend request instead
contains the image URL and Cloudinary public ID:

```typescript
export interface ProductCreateSubmission extends ProductCreateFormValues {
  image: File;
}

export interface ProductUpdateSubmission extends Partial<ProductCreateFormValues> {
  imageUrl?: string;
  imagePublicId?: string;
  image?: File;
}
```

## Q2. What does `const { image, ...product } = request;` do?

It takes the file out of the request and puts the remaining product fields in a
new object named `product`. A `File` cannot be sent as part of ordinary JSON,
so the image is uploaded separately.

## Q3. What happens when I upload a product image?

The browser first asks Spring Boot for signed upload details. The backend
creates a signature using the Cloudinary API secret and returns the signature,
a timestamp, a public API key, the preset name, and a unique public ID. The
browser then uploads the file directly to Cloudinary.

The API secret is never sent to or included in the frontend. The browser only
gets a signature that authorizes the specific upload parameters.

## Q4. Why does the upload return both `imageUrl` and `imagePublicId`?

Cloudinary returns both values:

- `secure_url` becomes `imageUrl`. The app uses it to display the picture.
- `public_id` becomes `imagePublicId`. The backend uses it to delete that
  picture later.

The database stores both, but product responses only need to send the image URL
back to the UI.

## Q5. What does `{ ...product, ...imageDetails }` do?

It copies the product fields and adds the URL and public ID returned by the
upload helper.

```typescript
{
  ...product,
  imageUrl: "https://res.cloudinary.com/.../image.jpg",
  imagePublicId: "products/123e4567-e89b-12d3-a456-426614174000"
}
```

The file itself is not included in this object or sent to the product API.

## Q6. What happens when I update a product?

If no new file was selected, the frontend sends only the changed product fields.
The existing image is left alone.

If a new file was selected:

1. The browser uploads the new file to Cloudinary.
2. The frontend sends its URL and public ID along with the changed product
   fields.
3. Spring Boot saves the new image details.
4. Spring Boot tells Cloudinary to delete the old image using its saved public
   ID.

The database save happens before the old-image deletion request. The current
implementation does not wait for an `afterCommit` callback; it deletes the old
image immediately after `productRepository.save` returns.

## Q7. Does the backend upload or store the image file?

No. The browser sends the file directly to Cloudinary. Spring Boot only:

1. Signs the upload parameters.
2. Saves the returned image URL and public ID in the product record.
3. Uses the stored public ID for deletion when an image is replaced or its
   product is deleted.

## Q8. What does `FormData` contain?

`FormData` can carry both a file and text fields. For a signed upload, the
frontend sends:

| Field | What it is for |
| --- | --- |
| `file` | The selected image |
| `upload_preset` | The signed preset configured in Cloudinary |
| `public_id` | The unique ID created by the backend |
| `api_key` | Identifies the Cloudinary account; it is not the API secret |
| `timestamp` | When the backend created the signature |
| `signature` | Proof that the backend authorized these upload parameters |

The frontend sends this form to Cloudinary's `POST /image/upload` endpoint.

## Q9. How are Cloudinary credentials configured?

The Cloud name, API key, API secret, and upload preset name are backend
environment variables. The API secret must never be placed in frontend code,
`frontend/.env`, or a variable whose name starts with `VITE_`.

The Cloudinary upload preset must be set to **Signed** in the Cloudinary
console. Configure the preset to allow only the image formats and file size
your app intends to accept.

## Q10. What if Cloudinary cannot delete the old image?

The product change is saved first, so a deletion failure does not undo the
product update. The backend logs the public ID so the orphaned image can be
cleaned up later. Existing images created before the app stored public IDs
cannot be automatically deleted by this new flow.

## Summary

- The browser uploads the file directly to Cloudinary.
- Spring Boot signs upload parameters but never sends its API secret to the
  browser.
- The product stores the image URL for viewing and the public ID for deletion.
- Replacing or deleting a product starts a Cloudinary image deletion after the
  database save or delete operation.
