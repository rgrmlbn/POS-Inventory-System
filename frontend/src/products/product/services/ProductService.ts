import axios from "axios";
import type {
  Product,
  ProductPage,
  ProductCreateRequest,
  ProductUpdateRequest,
} from "../types/Product";

const BASE_URL = import.meta.env.VITE_API_URL;

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

  async getProductById(id: number) {
    const response = await axios.get<Product>(
      BASE_URL + "/api/v1/products/" + id,
    );

    return response.data;
  },

  // -> POST ENDPOINTS
  async createProduct(request: ProductCreateRequest) {
    const response = await axios.post<Product>(
      BASE_URL + "/api/v1/products/create",
      request,
    );
    return response.data;
  },

  // -> PATCH ENDPOINTS
  async updateProductById(id: number, request: ProductUpdateRequest) {
    const response = await axios.patch<Product>(
      BASE_URL + "/api/v1/products/" + id + "/update",
      request,
    );
    return response.data;
  },

  // -> DELETE ENDPOINTS
  async deleteProductById(id: number) {
    await axios.delete(
      BASE_URL + "/api/v1/products/" + id + "/delete"
    );
  },

  async deleteAllProducts() {
    await axios.delete(
      BASE_URL + "/api/v1/products/delete-all"
    );
  },
};
