import axios from "axios";
import type {
  Product,
  ProductPage,
  ProductCreateSubmission,
  ProductUpdateSubmission,
  CloudinaryUploadSignature
} from "../types/Product";

// -> BASE API URL
const BASE_URL = import.meta.env.VITE_API_URL;

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

export const ProductService = {
  // -> GET ENDPOINTS
  async getAllProductsByProductName(
    productName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<ProductPage>(
      BASE_URL + "/api/v1/products/productName",
      {
        params: {
          productName,
          page,
          size,
        },
      },
    );

    return response.data;
  },

  async getAllProducts() {
    const response = await axios.get<Product[]>(
      BASE_URL + "/api/v1/products",
    );

    return response.data;
  },

  async getProductById(id: number) {
    const response = await axios.get<Product>(
      BASE_URL + "/api/v1/products/" + id,
    );

    return response.data;
  },

  // -> POST ENDPOINTS
  async createProduct(request: ProductCreateSubmission) {
    const { image, ...product } = request;
    const imageDetails = await uploadProductImage(image);

    const response = await axios.post<Product>(
      BASE_URL + "/api/v1/products/create",
      { ...product, ...imageDetails },
    );
    return response.data;
  },

  // -> PATCH ENDPOINTS
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
  },

  // -> DELETE ENDPOINTS
  async deleteProductById(id: number) {
    await axios.delete(BASE_URL + "/api/v1/products/" + id + "/delete");
  },

  async deleteAllProducts() {
    await axios.delete(BASE_URL + "/api/v1/products/delete-all");
  },
};
