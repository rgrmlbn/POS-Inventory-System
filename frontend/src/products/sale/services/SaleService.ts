import axios from "axios";
import type { Sale, SalePage, SaleCreateRequest } from "../types/Sale";

const BASE_URL = import.meta.env.VITE_API_URL;

export const SaleService = {
  // -> GET ENDPOINTS
  async getAllSalesByProductName(
    productName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<SalePage>(
      BASE_URL + "/api/v1/sales/productName",
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

  async getAllSales() {
    const response = await axios.get<Sale[]>(BASE_URL + "/api/v1/sales");

    return response.data;
  },

  async getSaleById(id: number) {
    const response = await axios.get<Sale>(BASE_URL + "/api/v1/sales/" + id);

    return response.data;
  },

  // -> POST ENDPOINTS
  async createSale(request: SaleCreateRequest) {
    const response = await axios.post<Sale>(
      BASE_URL + "/api/v1/sales/create",
      request,
    );
    return response.data
  },
};
