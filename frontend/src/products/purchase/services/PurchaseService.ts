import axios from "axios";
import type { Purchase, PurchasePage } from "../types/Purchase";

const BASE_URL = import.meta.env.VITE_API_URL;

export const PurchaseService = {
  async getAllPurchasesByProductName(
    productName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<PurchasePage>(
      BASE_URL + "/api/v1/purchases/productName",
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

  async getPurchaseById(id: number) {
    const response = await axios.get<Purchase>(BASE_URL + "/api/v1/purchases/" + id);

    return response.data;
  },
};
