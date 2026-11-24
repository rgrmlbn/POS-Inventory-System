# Product and Category Services: Complete Beginner Guide

This guide explains the product image process in this POS Inventory System.
It covers:

- POST and PATCH
- React forms
- TypeScript types
- Spring Boot upload signatures
- Cloudinary signed uploads
- Database storage
- Product updates
- Product image deletion

The main frontend file is:

```text
frontend/src/products/product/services/ProductService.ts
```

The main backend files are:

```text
backend/src/main/java/com/spring/backend/module/product/product/
backend/src/main/java/com/spring/backend/module/properties/
```

---

## 1. The Complete Process

The application does not upload the image to Spring Boot. Instead, the React
frontend uploads the image directly to Cloudinary.

```text
React product form
        |
        | 1. User selects an image
        v
React validation
        |
        | 2. Check image size and format
        v
Spring Boot
        |
        | 3. Create a signed Cloudinary upload
        v
React
        |
        | 4. Upload the image file directly to Cloudinary
        v
Cloudinary
        |
        | 5. Return secure_url and public_id
        v
React
        |
        | 6. Send product fields plus imageUrl and imagePublicId
        v
Spring Boot
        |
        | 7. Validate and save product information
        v
Database
```

The API secret is not sent to the frontend. Spring Boot uses the secret only
to create a signature.

---

## 2. POST and PATCH

### POST

POST creates a new product.

```text
POST /api/v1/products/create
```

Example:

```typescript
await axios.post(
  "/api/v1/products/create",
  productData,
);
```

A POST request creates a new database row. The server generates the new
product ID.

Creating a product requires:

- Product name
- SKU
- Barcode
- Category ID
- Cost price
- Selling price
- Reorder level
- Image URL
- Image public ID

### PATCH

PATCH changes an existing product.

```text
PATCH /api/v1/products/{id}/update
```

Example:

```typescript
await axios.patch(
  "/api/v1/products/25/update",
  {
    sellingPrice: 75,
  },
);
```

The existing ID is included in the URL.

The update request only needs to contain the fields that must change.

| | POST | PATCH |
| --- | --- | --- |
| Purpose | Create a product | Update a product |
| ID required | No; server creates it | Yes; ID is in the URL |
| Fields | Normally all required fields | Only fields being changed |
| Image | Required | Optional |

---

## 3. Frontend Product Types

The frontend types are in:

```text
frontend/src/products/product/types/Product.ts
```

### Product create request

```typescript
export interface ProductCreateRequest {
  productName: string;
  sku: string;
  barcode: string;
  categoryId: number;
  costPrice: number;
  sellingPrice: number;
  reorderLevel: number;
  imageUrl: string;
  imagePublicId: string;
}
```

### Product form values

The form does not receive `imageUrl` or `imagePublicId`. The backend does not
know these values until Cloudinary has processed the image.

```typescript
export type ProductCreateFormValues = Omit<
  ProductCreateRequest,
  "imageUrl" | "imagePublicId"
>;
```

`Omit` removes the two image fields from the create request type.

### Create submission

The create form adds the browser file separately:

```typescript
export interface ProductCreateSubmission
  extends ProductCreateFormValues {
  image: File;
}
```

`File` represents a local file selected through an HTML file input.

### Update submission

The update request is partial because the user may update only some fields:

```typescript
export interface ProductUpdateSubmission
  extends Partial<ProductCreateFormValues> {
  imageUrl?: string;
  imagePublicId?: string;
  image?: File;
}
```

The question mark means the property is optional.

### Cloudinary signature response

```typescript
export interface CloudinaryUploadSignature {
  cloudName: string;
  apiKey: string;
  uploadPreset: string;
  publicId: string;
  timestamp: number;
  signature: string;
}
```

---

## 4. Selecting the Product Image

The create form stores the selected file in React state:

```typescript
const [selectedImage, setSelectedImage] =
  useState<File | undefined>();
```

The file input updates the state:

```typescript
<input
  type="file"
  accept="image/jpeg,image/png,image/gif,image/webp"
  onChange={(event) => {
    const [file] = event.target.files ?? []; // value ?? null
    setSelectedImage(file);
  }}
/>
```

This expression:

```typescript
event.target.files ?? []
```

means:

1. Read the files selected by the user.
2. Take the first file.
3. Return `undefined` if no file exists.

### Frontend image validation

The create form verifies that an image was selected:

```typescript
if (!selectedImage) {
  setError("root.server", {
    type: "validate",
    message: "A product image is required.",
  });
  return;
}
```

It verifies the size:

```typescript
if (selectedImage.size > 5 * 1024 * 1024) {
  setError("root.server", {
    type: "validate",
    message: "Image must be 5 MB or smaller.",
  });
  return;
}
```

`5 * 1024 * 1024` means:

```text
5 MB × 1024 KB × 1024 bytes
```

It verifies the file MIME type:

```typescript
if (
  !["image/jpeg", "image/png", "image/gif", "image/webp"].includes(
    selectedImage.type,
  )
) {
  setError("root.server", {
    type: "validate",
    message: "Upload a JPEG, PNG, GIF, or WebP image.",
  });
  return;
}
```

This is useful frontend validation, but backend validation must still protect
the application.

---

## 5. Removing the File from the Product JSON

The create service begins with:

```typescript
const { image, ...product } = request;
```

Suppose `request` contains:

```typescript
const request = {
  productName: "Rice",
  sku: "RICE-001",
  categoryId: 1,
  image: selectedFile,
};
```

Destructuring creates:

```typescript
// image
selectedFile

// product
{
  productName: "Rice",
  sku: "RICE-001",
  categoryId: 1,
}
```

The `image` variable contains the browser file.

The `product` variable contains every remaining property.

The image file is removed because ordinary JSON cannot contain a JavaScript
`File` object.

---

## 6. Requesting the Cloudinary Signature

The frontend calls:

```text
POST /api/v1/products/image-upload-signature
```

The React code is:

```typescript
const signatureResponse =
  await axios.post<CloudinaryUploadSignature>(
    BASE_URL + "/api/v1/products/image-upload-signature",
  );

const upload = signatureResponse.data;
```

The Spring Boot endpoint is:

```java
@PostMapping("/image-upload-signature")
public ResponseEntity<CloudinaryUploadSignatureResponse>
        createImageUploadSignature() {

    return ResponseEntity.ok(
            cloudinaryImageService.createUploadSignature()
    );
}
```

This endpoint does not receive the image.

It receives no image file and does not upload anything. Its only job is to
create a signed upload permission.

---

## 7. How Spring Boot Creates the Signature

The backend configuration class is:

```java
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
@Getter
@Setter
public class CloudinaryProperties {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
    private String uploadPreset;
}
```

The credentials come from backend environment variables, not frontend code.

Spring creates a timestamp:

```java
long timestamp = Instant.now().getEpochSecond();
```

It creates a unique public ID:

```java
String publicId = "products/" + UUID.randomUUID();
```

An example public ID is:

```text
products/123e4567-e89b-12d3-a456-426614174000
```

It creates the signed parameters:

```java
Map<String, Object> parameters = Map.of(
    "timestamp", timestamp,
    "public_id", publicId,
    "upload_preset", properties.getUploadPreset()
);
```

It creates the signature using the API secret:

```java
String signature =
    cloudinary.apiSignRequest(
        parameters,
        properties.getApiSecret()
    );
```

The complete implementation is:

```java
public CloudinaryUploadSignatureResponse createUploadSignature() {
    Cloudinary cloudinary = createCloudinaryClient();

    long timestamp =
        Instant.now().getEpochSecond();

    String publicId =
        "products/" + UUID.randomUUID();

    Map<String, Object> parameters =
        uploadParameters(timestamp, publicId);

    String signature =
        cloudinary.apiSignRequest(
            parameters,
            properties.getApiSecret()
        );

    return CloudinaryUploadSignatureResponse.builder()
        .cloudName(properties.getCloudName())
        .apiKey(properties.getApiKey())
        .uploadPreset(properties.getUploadPreset())
        .publicId(publicId)
        .timestamp(timestamp)
        .signature(signature)
        .build();
}
```

The frontend receives:

- Cloud name
- API key
- Upload preset
- Public ID
- Timestamp
- Signature

The API secret is never returned to React.

---

## 8. The Signed Parameters

The signature authorizes these parameters:

| Parameter | Purpose |
| --- | --- |
| `timestamp` | Records when the signature was created |
| `public_id` | Gives the Cloudinary image a unique name |
| `upload_preset` | Applies the configured upload preset |

The upload preset must be configured as **Signed** in the Cloudinary console.

The signed preset may also configure:

- Allowed image formats
- Maximum file size
- Folder
- Image transformations

---

## 9. Uploading the File to Cloudinary

After receiving the signature, React creates `FormData`.

```typescript
const formData = new FormData();
```

`FormData` is designed to carry files and text fields together.

React appends the image:

```typescript
formData.append("file", image);
```

React appends the signed values:

```typescript
formData.append(
    "upload_preset",
    upload.uploadPreset,
);

formData.append(
    "public_id",
    upload.publicId,
);

formData.append(
    "api_key",
    upload.apiKey,
);

formData.append(
    "timestamp",
    upload.timestamp.toString(),
);

formData.append(
    "signature",
    upload.signature,
);
```

The Cloudinary URL is:

```text
https://api.cloudinary.com/v1_1/{cloudName}/image/upload
```

The complete frontend request is:

```typescript
const response =
  await axios.post<{
    secure_url?: string;
    public_id?: string;
  }>(
    "https://api.cloudinary.com/v1_1/"
      + upload.cloudName
      + "/image/upload",
    formData,
  );
```

The browser sends the file directly to Cloudinary. Spring Boot is not in the
middle of this transfer.

---

## 10. Reading the Cloudinary Response

Cloudinary returns fields like:

```json
{
  "secure_url": "https://res.cloudinary.com/demo/image/upload/products/example.jpg",
  "public_id": "products/123e4567-e89b-12d3-a456-426614174000"
}
```

React reads both values:

```typescript
const imageUrl =
  response.data.secure_url;

const imagePublicId =
  response.data.public_id;
```

The meaning of each value is:

| Cloudinary field | Application field | Purpose |
| --- | --- | --- |
| `secure_url` | `imageUrl` | Display the image |
| `public_id` | `imagePublicId` | Delete or manage the image |

React verifies that Cloudinary returned both values and that the public ID
matches the signed public ID:

```typescript
if (!imageUrl || imagePublicId !== upload.publicId) {
  throw new Error(
    "Cloudinary returned invalid product image details.",
  );
}
```

It then returns:

```typescript
return {
  imageUrl,
  imagePublicId,
};
```

---

## 11. Complete Frontend Upload Helper

The frontend helper currently has this structure:

```typescript
async function uploadProductImage(image: File) {
  const signatureResponse =
    await axios.post<CloudinaryUploadSignature>(
      BASE_URL
        + "/api/v1/products/image-upload-signature",
    );

  const upload = signatureResponse.data;

  const formData = new FormData();
  formData.append("file", image);
  formData.append(
    "upload_preset",
    upload.uploadPreset,
  );
  formData.append(
    "public_id",
    upload.publicId,
  );
  formData.append(
    "api_key",
    upload.apiKey,
  );
  formData.append(
    "timestamp",
    upload.timestamp.toString(),
  );
  formData.append(
    "signature",
    upload.signature,
  );

  const response =
    await axios.post<{
      secure_url?: string;
      public_id?: string;
    }>(
      "https://api.cloudinary.com/v1_1/"
        + upload.cloudName
        + "/image/upload",
      formData,
    );

  const imageUrl = response.data.secure_url;
  const imagePublicId = response.data.public_id;

  if (!imageUrl || imagePublicId !== upload.publicId) {
    throw new Error(
      "Cloudinary returned invalid product image details.",
    );
  }

  return {
    imageUrl,
    imagePublicId,
  };
}
```

---

## 12. Creating a Product

The frontend create method is:

```typescript
async createProduct(
  request: ProductCreateSubmission,
) {
  const { image, ...product } = request;

  const imageDetails =
    await uploadProductImage(image);

  const response =
    await axios.post<Product>(
      BASE_URL + "/api/v1/products/create",
      {
        ...product,
        ...imageDetails,
      },
    );

  return response.data;
}
```

The spread operator copies values.

```typescript
{
  ...product,
  ...imageDetails,
}
```

means:

1. Copy every product field.
2. Copy `imageUrl`.
3. Copy `imagePublicId`.

An example resulting JSON body is:

```json
{
  "productName": "Rice",
  "sku": "RICE-001",
  "barcode": "123456789",
  "categoryId": 1,
  "costPrice": 50,
  "sellingPrice": 70,
  "reorderLevel": 10,
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/products/example.jpg",
  "imagePublicId": "products/123e4567-e89b-12d3-a456-426614174000"
}
```

The browser `File` object is not in this JSON body.

---

## 13. Saving the Product in Spring Boot

The create endpoint receives the validated request:

```java
@PostMapping("/create")
public ResponseEntity<ProductResponse> createProduct(
        @RequestBody @Valid ProductCreateRequest request) {

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(productService.createProduct(request));
}
```

`@Valid` runs Bean Validation constraints from `ProductCreateRequest`.

The backend also validates the image URL:

```java
@NotBlank(message = "Product image URL is required")
@Size(
    max = 2048,
    message = "Product image URL must be 2048 characters or fewer"
)
@Pattern(
    regexp =
        "^https://res\\.cloudinary\\.com/"
        + "[A-Za-z0-9_-]+/image/upload/.+$",
    message =
        "Product image URL must be a Cloudinary image URL"
)
private String imageUrl;
```

The public ID is also checked:

```java
@NotBlank(
    message = "Product image public ID is required"
)
@Pattern(
    regexp =
        "^products/[0-9a-f]{8}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{12}$",
    message = "Product image public ID is invalid"
)
private String imagePublicId;
```

The service finds the selected category:

```java
CategoryEntity category =
    categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() ->
            new ResourceNotFoundException("Category"));
```

It converts the request to an entity:

```java
ProductEntity product =
    productMapper.toEntity(request, category);
```

It saves the product:

```java
ProductEntity savedProduct =
    productRepository.save(product);
```

The entity stores:

```java
@Column(length = 2048)
private String imageUrl;

@Column(
    name = "image_public_id",
    length = 255
)
private String imagePublicId;
```

The URL is stored for displaying the image. The public ID is stored so Spring
can delete or manage the image later.

The service also creates inventory for the product:

```java
inventoryService.createInventory(savedProduct);
```

---

## 14. Updating a Product Without a New Image

The edit form may submit:

```typescript
{
  productName: "Premium Rice",
  sellingPrice: 75,
  image: undefined,
}
```

The frontend service removes the `image` property:

```typescript
const { image, ...product } = request;
let requestWithImage = product;
```

Because no file exists:

```typescript
if (image) {
  // This block is not executed
}
```

The PATCH request is sent without image details:

```typescript
axios.patch(
  BASE_URL
    + "/api/v1/products/"
    + id
    + "/update",
  requestWithImage,
);
```

This means:

- Product fields can change.
- The existing image URL remains unchanged.
- The existing public ID remains unchanged.
- Cloudinary is not called.

---

## 15. Updating a Product with a New Image

If the user selected a new file:

```typescript
if (image) {
  const imageDetails =
    await uploadProductImage(image);

  requestWithImage = {
    ...product,
    ...imageDetails,
  };
}
```

The result contains the new image fields:

```json
{
  "productName": "Premium Rice",
  "sellingPrice": 75,
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/products/new-image.jpg",
  "imagePublicId": "products/new-public-id"
}
```

Before applying the update, Spring stores the old public ID:

```java
String oldImagePublicId =
    product.getImagePublicId();
```

It updates the two image fields:

```java
if (update.getImageUrl() != null) {
    product.setImageUrl(update.getImageUrl());
    product.setImagePublicId(
        update.getImagePublicId()
    );
}
```

It saves the updated product:

```java
ProductEntity savedProduct =
    productRepository.save(product);
```

It deletes the old image if it has changed:

```java
if (oldImagePublicId != null
        && !oldImagePublicId.equals(
            savedProduct.getImagePublicId()
        )) {
    deleteImage(oldImagePublicId);
}
```

The database update happens before the old Cloudinary image deletion.

---

## 16. Keeping Image URL and Public ID Together

The update DTO uses:

```java
@Size(
    max = 2048,
    message =
        "Product image URL must be 2048 characters or fewer"
)
@Pattern(
    regexp =
        "^https://res\\.cloudinary\\.com/"
        + "[A-Za-z0-9_-]+/image/upload/.+$",
    message =
        "Product image URL must be a Cloudinary image URL"
)
private String imageUrl;

@Pattern(
    regexp =
        "^products/[0-9a-f]{8}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{12}$",
    message = "Product image public ID is invalid"
)
private String imagePublicId;
```

It also has this validation:

```java
@AssertTrue(
    message =
        "Product image URL and public ID "
        + "must be provided together"
)
public boolean isImageDetailsValid() {
    return (imageUrl == null) == (imagePublicId == null);
}
```

This means:

```text
Both fields are present  -> valid
Both fields are missing   -> valid
One field is present      -> invalid
```

---

## 17. Deleting a Product Image

The delete service has:

```java
productRepository.delete(product);
deleteImage(product.getImagePublicId());
```

The database product row is deleted first.

Then Spring uses the public ID to request Cloudinary deletion.

The low-level deletion code is:

```java
createCloudinaryClient()
    .uploader()
    .destroy(
        publicId,
        Map.of("invalidate", true)
    );
```

The `invalidate` option instructs Cloudinary to invalidate cached versions of
the image.

### Validating the public ID

Spring prevents deletion of unmanaged Cloudinary images:

```java
private static final Pattern PRODUCT_IMAGE_ID =
    Pattern.compile(
        "^products/[0-9a-f]{8}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{4}-"
        + "[0-9a-f]{12}$"
    );
```

Before deletion:

```java
if (!PRODUCT_IMAGE_ID.matcher(publicId).matches()) {
    throw new CloudinaryOperationException(
        "Refusing to delete an unmanaged "
        + "Cloudinary image."
    );
}
```

This protects Cloudinary images outside the application's `products/` folder.

---

## 18. Handling Cloudinary Deletion Failure

The deletion operation is wrapped in a helper:

```java
private void deleteImage(String imagePublicId) {
    if (imagePublicId == null
            || imagePublicId.isBlank()) {
        return;
    }

    try {
        cloudinaryImageService
            .deleteImage(imagePublicId);
    } catch (CloudinaryOperationException exception) {
        log.error(
            "Could not delete Cloudinary image "
            + "with public ID {}",
            imagePublicId,
            exception
        );
    }
}
```

If Cloudinary deletion fails:

- The product database change may already be complete.
- Spring logs the public ID.
- The product operation is not undone.
- The orphaned Cloudinary image can be cleaned up later.

Existing product images that were stored before `imagePublicId` was added
cannot be automatically deleted because they do not have a public ID in the
database.

---

## 19. Cloudinary Credentials

The backend needs these environment variables:

```text
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
CLOUDINARY_UPLOAD_PRESET
```

Example Spring property configuration:

```properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
cloudinary.upload-preset=${CLOUDINARY_UPLOAD_PRESET}
```

### API secret safety

Do not place the API secret in:

- React source code
- `frontend/.env`
- A `VITE_` variable
- HTML or JavaScript served to the browser

For example, never add:

```text
VITE_CLOUDINARY_API_SECRET
```

The frontend may safely receive:

- Cloud name
- API key
- Upload preset
- Timestamp
- Public ID
- Signature

The API key is not the API secret.

---

## 20. Important Difference: Category Service

The category service does not upload images.

A category request contains only category fields. It does not need:

- A signed upload
- A Cloudinary file upload
- `FormData`
- An image URL
- An image public ID

Therefore, category POST and PATCH operations send their JSON data directly
to Spring Boot.

Product POST and PATCH operations are different because products support image
files.

---

## 21. Complete Create Sequence

```text
1. User selects product fields and an image.
2. React checks that the image exists.
3. React checks the image size.
4. React checks the image format.
5. React sends a signature request to Spring Boot.
6. Spring creates a timestamp.
7. Spring creates a unique public ID.
8. Spring signs the upload parameters.
9. Spring returns the signature details.
10. React creates FormData.
11. React uploads the file directly to Cloudinary.
12. Cloudinary returns secure_url and public_id.
13. React removes the browser File from the JSON object.
14. React sends product fields and image details to Spring.
15. Spring validates the image URL and public ID.
16. Spring finds the category.
17. Spring saves the product.
18. Spring creates inventory for the product.
19. React invalidates product queries.
20. The product list refreshes.
```

## 22. Complete Update Sequence

```text
1. React pre-fills the edit form with product values.
2. The user changes fields.
3. The user may optionally select a new image.
4. React validates the image if a new image was selected.
5. If no new image exists, React sends only JSON product fields.
6. If a new image exists, React requests a signature.
7. React uploads the new file to Cloudinary.
8. React receives the new URL and public ID.
9. React sends the fields plus new image details.
10. Spring validates the PATCH request.
11. Spring loads the existing product.
12. Spring remembers the old image public ID.
13. Spring updates product fields.
14. Spring saves the product.
15. Spring compares old and new public IDs.
16. If they differ, Spring deletes the old Cloudinary image.
```

## Summary

- `POST` creates a product.
- `PATCH` updates a product.
- React holds the browser `File` temporarily.
- `File` is not sent in ordinary product JSON.
- React requests a signed upload from Spring Boot.
- Spring creates the signature using the API secret.
- React uploads the file directly to Cloudinary.
- Cloudinary returns `secure_url` and `public_id`.
- The database stores `imageUrl` and `imagePublicId`.
- Replacing or deleting a product deletes the old image using the stored
  public ID.
