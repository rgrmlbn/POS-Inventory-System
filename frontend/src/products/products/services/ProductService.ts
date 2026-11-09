import axios from "axios";
import type { Product, ProductPage } from "../types/Product";

const BASE_URL = import.meta.env.VITE_API_URL;

export const ProductService = {

  // async getAllProducts(page: number = 0, size: number = 10) {
  //   const response = await axios.get<ProductPage>(
  //     BASE_URL + "/api/v1/products",
  //     {
  //       params: {
  //         page,
  //         size,
  //       },
  //     },
  //   );

  //   return response.data;
  // },

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

  async getProductById(productId: number) {
    const response = await axios.get<Product>(
      BASE_URL + "/api/v1/products/" + productId,
    );

    return response.data;
  },
};
