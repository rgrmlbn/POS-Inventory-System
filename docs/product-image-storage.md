# Product image upload, editing, and deletion

This guide explains what happens to a product image, in order, and which
frontend and backend code is responsible for each step.

## What the app stores

The app stores two values for each product image:

- **`imageUrl`**: Cloudinary's HTTPS link. The frontend uses this to display the
  image.
- **`imagePublicId`**: Cloudinary's identifier for the image. The backend uses
  this to delete the image later.

The image file itself is stored by Cloudinary. It does not pass through Spring
Boot.

## Create a product with an image

### 1. The product form gets the image

[`ProductCreateForm.tsx`](../frontend/src/products/product/components/ProductCreateForm.tsx)
lets the user select an image and checks that it is no larger than 5 MB and is
one of the accepted image types. The form sends the selected browser `File`
with the product values through the product-create mutation.

### 2. The frontend asks for upload authorization

[`ProductService.ts`](../frontend/src/products/product/services/ProductService.ts)
removes the `File` from the product data and calls
`POST /api/v1/products/image-upload-signature`.

The corresponding route is in
[`ProductController.java`](../backend/src/main/java/com/spring/backend/module/product/product/controller/ProductController.java).
The controller delegates the signing work to
[`CloudinaryImageService.java`](../backend/src/main/java/com/spring/backend/module/product/product/service/impl/CloudinaryImageService.java).

### 3. The backend signs the upload details

`CloudinaryImageService.createUploadSignature()` creates a unique public ID
under `products/` and a timestamp. Using Cloudinary's Java SDK and the
backend-only API secret, it signs the upload preset, public ID, and timestamp.
It returns those upload details, along with the cloud name and API key.

The API secret is **not** returned to the frontend. The API key identifies the
Cloudinary account; it is not the secret.

### 4. The frontend uploads the file directly to Cloudinary

`ProductService.ts` puts the file and signed values into `FormData`, then sends
them to Cloudinary's `POST /image/upload` endpoint. Cloudinary stores the image
and responds with `secure_url` and `public_id`. The frontend checks that the
returned public ID matches the ID the backend signed.

### 5. The frontend sends product data to Spring Boot

After the upload succeeds, `ProductService.ts` sends a JSON request to
`POST /api/v1/products/create`. It includes the product fields, `imageUrl`, and
`imagePublicId`; it does not include the image file.

### 6. The backend saves the product

`ProductController.createProduct()` passes the validated request to
`ProductServiceImpl.createProduct()`. The service uses
`ProductMapper.toEntity()` to copy the request values into `ProductEntity`,
saves the product, and creates its inventory record.

`ProductEntity` stores the URL and public ID in the `image_url` and
`image_public_id` database columns.

## Edit a product

The image is optional in
[`ProductEditForm.tsx`](../frontend/src/products/product/components/ProductEditForm.tsx).

### Editing other fields without choosing a new image

1. The frontend sends only the changed fields, such as a new price.
2. No upload-signature request or Cloudinary upload happens.
3. Since no image fields are sent, the backend leaves the existing image URL
   and public ID unchanged.

### Replacing the image

1. The frontend gets a signature and uploads the new file using the same steps
   as product creation.
2. It sends `PATCH /api/v1/products/{id}/update` with the changed product
   fields, new `imageUrl`, and new `imagePublicId`.
3. `ProductUpdateRequest` validates that the URL and public ID are supplied
   together.
4. `ProductServiceImpl.updateProductById()` saves the new image details and
   asks `CloudinaryImageService` to delete the previous public ID.
5. `CloudinaryImageService.deleteImage()` asks Cloudinary to delete the old
   image and invalidate its cached delivery.

The database row is saved before the old Cloudinary image is deleted. However,
the current implementation does not register deletion for an
`afterCommit` callback; the deletion request runs after `productRepository.save`
returns and within the transaction boundary.

## Delete a product

`ProductServiceImpl.deleteProductById()` deletes the database record, then
requests deletion of its image. `deleteAllProducts()` first collects the
stored public IDs, deletes all product records, and then requests each image
deletion.

The Cloudinary API secret is used only by backend code for the delete request.

## Configuration

Put these variables in the backend environment (locally, `backend/.env`; in
deployment, the backend's environment settings):

```dotenv
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_rotated_api_secret
CLOUDINARY_UPLOAD_PRESET=pos_system
```

[`application-dev.properties`](../backend/src/main/resources/application-dev.properties)
maps those environment variables to Spring properties. Do not put the API
secret in frontend code, `frontend/.env`, or any `VITE_*` variable.

In the Cloudinary console, set the `pos_system` upload preset to **Signed**.
Set the preset to allow only the image formats and maximum file size your app
supports. Do not configure it to override `public_id`, because the backend
signs a unique ID under `products/` for every upload.

## Database update

Development uses Hibernate schema updates. Before deploying to a database
configured with `ddl-auto=validate`, add the new nullable column once:

```sql
ALTER TABLE products ADD COLUMN image_public_id VARCHAR(255) NULL;
```

Products created before the app stored public IDs keep displaying their
existing image URLs. Their old IDs are unknown, so the app cannot automatically
delete those legacy images.

## Code walkthrough

The following are the important parts of the implementation, with an
explanation of what each part does.

### Frontend: get a signature and upload the file

In
[`ProductService.ts`](../frontend/src/products/product/services/ProductService.ts),
`uploadProductImage()` performs the two network calls needed for an upload:

```typescript
async function uploadProductImage(image: File) {
  const signatureResponse = await axios.post<CloudinaryUploadSignature>(
    BASE_URL + "/api/v1/products/image-upload-signature",
  );
  const upload = signatureResponse.data;
  const formData = new FormData();
  formData.append("file", image);
  formData.append("upload_preset", upload.uploadPreset);
  formData.append("public_id", upload.publicId);
  formData.append("api_key", upload.apiKey);
  formData.append("timestamp", upload.timestamp.toString());
  formData.append("signature", upload.signature);

  const response = await axios.post<{
    secure_url?: string;
    public_id?: string;
  }>(
    "https://api.cloudinary.com/v1_1/" + upload.cloudName + "/image/upload",
    formData,
  );
  const imageUrl = response.data.secure_url;
  const imagePublicId = response.data.public_id;

  if (!imageUrl || imagePublicId !== upload.publicId) {
    throw new Error("Cloudinary returned invalid product image details.");
  }

  return { imageUrl, imagePublicId };
}
```

What the important lines mean:

- The first `axios.post` asks **your backend** for permission details. It does
  not send the image yet.
- `new FormData()` creates a package that can hold both the file and text
  values.
- The next `axios.post` sends that package **directly to Cloudinary**.
- `secure_url` is the display link. `public_id` is the identifier used for
  deletion.
- The check stops the frontend from saving an unexpected ID or a missing URL.

The `CloudinaryUploadSignature` TypeScript type in
[`Product.ts`](../frontend/src/products/product/types/Product.ts) describes
the response shape from Spring Boot. It is only a type description; it does not
create the signature.

### Frontend: create and edit requests

The create function uploads first, then adds the returned URL and ID to the
product JSON:

```typescript
async createProduct(request: ProductCreateSubmission) {
  const { image, ...product } = request;
  const imageDetails = await uploadProductImage(image);

  const response = await axios.post<Product>(
    BASE_URL + "/api/v1/products/create",
    { ...product, ...imageDetails },
  );
  return response.data;
}
```

`const { image, ...product }` separates the file from the ordinary product
fields. `{ ...product, ...imageDetails }` combines the text fields with
`imageUrl` and `imagePublicId`. The file itself is not included in this JSON.

For editing, the upload happens only if a new file was selected:

```typescript
async updateProductById(id: number, request: ProductUpdateSubmission) {
  const { image, ...product } = request;
  let requestWithImage = product;

  if (image) {
    const imageDetails = await uploadProductImage(image);
    requestWithImage = {
      ...product,
      ...imageDetails,
    };
  }

  const response = await axios.patch<Product>(
    BASE_URL + "/api/v1/products/" + id + "/update",
    requestWithImage,
  );
  return response.data;
}
```

If `image` is missing, the `if` block is skipped and the current image remains
unchanged. If a new `image` exists, the uploaded image details are added to the
PATCH request.

### Backend: issue the signature

The route in
[`ProductController.java`](../backend/src/main/java/com/spring/backend/module/product/product/controller/ProductController.java)
is intentionally small:

```java
@PostMapping("/image-upload-signature")
public ResponseEntity<CloudinaryUploadSignatureResponse> createImageUploadSignature() {
    return ResponseEntity.ok(cloudinaryImageService.createUploadSignature());
}
```

The controller receives the HTTP request and delegates the actual work to
`CloudinaryImageService`. The signing method in
[`CloudinaryImageService.java`](../backend/src/main/java/com/spring/backend/module/product/product/service/impl/CloudinaryImageService.java)
is:

```java
public CloudinaryUploadSignatureResponse createUploadSignature() {
    Cloudinary cloudinary = createCloudinaryClient();
    long timestamp = Instant.now().getEpochSecond();
    String publicId = "products/" + UUID.randomUUID();
    Map<String, Object> parameters = uploadParameters(timestamp, publicId);
    String signature = cloudinary.apiSignRequest(parameters, properties.getApiSecret());

    return CloudinaryUploadSignatureResponse.builder()
            .cloudName(properties.getCloudName())
            .apiKey(properties.getApiKey())
            .uploadPreset(properties.getUploadPreset())
            .publicId(publicId)
            .timestamp(timestamp)
            .signature(signature)
            .build();
}

private Map<String, Object> uploadParameters(long timestamp, String publicId) {
    return Map.of(
            "timestamp", timestamp,
            "public_id", publicId,
            "upload_preset", properties.getUploadPreset()
    );
}
```

- `UUID.randomUUID()` makes an unpredictable ID so each new image has its own
  name.
- `uploadParameters()` lists exactly the upload values that are signed.
- `apiSignRequest()` uses the secret configured on the backend to create the
  signature.
- The response sends the signature and public API key, but never the API
  secret.

The Cloudinary settings are loaded by
[`application-dev.properties`](../backend/src/main/resources/application-dev.properties)
from backend environment variables. `createCloudinaryClient()` checks that the
required settings exist before using them.

### Backend: validate and save the product

[`ProductCreateRequest.java`](../backend/src/main/java/com/spring/backend/module/product/product/dto/request/ProductCreateRequest.java)
validates the image URL and public ID before the controller accepts the
request. `ProductServiceImpl.createProduct()` then saves the product:

```java
CategoryEntity category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category"));

ProductEntity product = productMapper.toEntity(request, category);
ProductEntity savedProduct = productRepository.save(product);
inventoryService.createInventory(savedProduct);
```

`ProductMapper.toEntity()` copies both image values into `ProductEntity`:

```java
.imageUrl(request.getImageUrl())
.imagePublicId(request.getImagePublicId())
```

The `imageUrl` and `imagePublicId` entity fields are persisted in the
`image_url` and `image_public_id` database columns.

### Backend: replace an image and remove the old one

The relevant part of `ProductServiceImpl.updateProductById()` remembers the old
ID, saves the new image details, and deletes the old image only if the ID changed:

```java
String oldImagePublicId = product.getImagePublicId();
if (update.getImageUrl() != null) {
    product.setImageUrl(update.getImageUrl());
    product.setImagePublicId(update.getImagePublicId());
}

ProductEntity savedProduct = productRepository.save(product);
if (!Objects.equals(oldImagePublicId, savedProduct.getImagePublicId())) {
    deleteImage(oldImagePublicId);
}
```

[`ProductUpdateRequest.java`](../backend/src/main/java/com/spring/backend/module/product/product/dto/request/ProductUpdateRequest.java)
also checks that `imageUrl` and `imagePublicId` are either both present or both
absent. This prevents saving only half of the new image information.

The deletion helper logs and contains Cloudinary failures so that the product
operation can continue:

```java
private void deleteImage(String imagePublicId) {
    if (imagePublicId == null || imagePublicId.isBlank()) {
        return;
    }

    try {
        cloudinaryImageService.deleteImage(imagePublicId);
    } catch (CloudinaryOperationException exception) {
        log.error("Could not delete Cloudinary image with public ID {}", imagePublicId, exception);
    }
}
```

This order saves the new product first and then attempts to remove the old
image. If Cloudinary's delete request fails, the backend logs the public ID.
The deletion is not automatically retried.

`deleteProductById()` and `deleteAllProducts()` use the same
`deleteImage()` helper after deleting product records.

`CloudinaryImageService.deleteImage()` uses the Cloudinary SDK's `destroy`
operation with `invalidate: true`. It accepts only the app's expected
`products/<UUID>` public ID format; that prevents accidental deletion of an
unrelated Cloudinary asset.

The method itself is:

```java
public void deleteImage(String publicId) {
    if (publicId == null || publicId.isBlank()) {
        return;
    }
    if (!PRODUCT_IMAGE_ID.matcher(publicId).matches()) {
        throw new CloudinaryOperationException("Refusing to delete an unmanaged Cloudinary image.");
    }

    try {
        Map<?, ?> result = createCloudinaryClient().uploader().destroy(
                publicId,
                Map.of("invalidate", true)
        );
        Object status = result.get("result");
        if (!"ok".equals(status) && !"not found".equals(status)) {
            throw new CloudinaryOperationException("Cloudinary did not confirm image deletion.");
        }
    } catch (CloudinaryOperationException exception) {
        throw exception;
    } catch (Exception exception) {
        throw new CloudinaryOperationException("Cloudinary image deletion failed.", exception);
    }
}
```

The null/blank check skips products with no stored image ID. The ID-format
check restricts which Cloudinary assets this code can delete. `destroy()` makes
the authenticated Cloudinary request, and `invalidate` asks Cloudinary to
invalidate cached copies of the old delivery URL.

## What if something fails?

- **Signature or upload fails:** the product is not saved with the new image.
- **Upload succeeds but saving the product fails:** the uploaded image may be
  left in Cloudinary without a product using it.
- **Deleting the old image fails:** the product update or deletion remains
  committed; the backend logs the public ID so the orphan can be cleaned up.
