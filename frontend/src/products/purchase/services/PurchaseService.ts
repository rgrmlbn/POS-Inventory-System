import axios from "axios";
import type {
  Purchase,
  PurchasePage,
  PurchaseCreateRequest,
} from "../types/Purchase";

const BASE_URL = import.meta.env.VITE_API_URL;

export const PurchaseService = {
  // -> GET ENDPOINTS
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

  async getAllPurchases() {
    const response = await axios.get<Purchase[]>(
      BASE_URL + "/api/v1/purchases",
    );

    return response.data;
  },

  async getPurchaseById(id: number) {
    const response = await axios.get<Purchase>(
      BASE_URL + "/api/v1/purchases/" + id,
    );

    return response.data;
  },

  // -> POST ENDPOINTS
  async createPurchase(request: PurchaseCreateRequest) {
    const response = await axios.post<Purchase>(
      BASE_URL + "/api/v1/purchases/create",
      request,
    );
    return response.data;
  },
};
