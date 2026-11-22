import axios from "axios";
import type {
  Product,
  ProductPage,
  ProductCreateSubmission,
  ProductUpdateSubmission,
} from "../types/Product";

// -> BASE API URL
const BASE_URL = import.meta.env.VITE_API_URL;

// -> CLOUDINARY IMAGE UPLOAD
const CLOUDINARY_CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME;
const CLOUDINARY_UPLOAD_PRESET = import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET;

async function uploadProductImage(image: File) {

  if (!CLOUDINARY_CLOUD_NAME || !CLOUDINARY_UPLOAD_PRESET) {
    throw new Error("Cloudinary upload is not configured.");
  }

  const formData = new FormData();
  formData.append("file", image);
  formData.append("upload_preset", CLOUDINARY_UPLOAD_PRESET);

  const response = await axios.post<{ secure_url?: string }>(
    `https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`,
    formData,
  );
  const imageUrl = response.data.secure_url;

  if (!imageUrl) {
    throw new Error("Cloudinary did not return an image URL.");
  }

  return imageUrl;
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
    const imageUrl = await uploadProductImage(image);

    const response = await axios.post<Product>(
      BASE_URL + "/api/v1/products/create",
      { ...product, imageUrl },
    );
    return response.data;
  },

  // -> PATCH ENDPOINTS
  async updateProductById(id: number, request: ProductUpdateSubmission) {
    const { image, ...product } = request;
    let requestWithImage = product;

    if (image) {
      requestWithImage = {
        ...product,
        imageUrl: await uploadProductImage(image),
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
