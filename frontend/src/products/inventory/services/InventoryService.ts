import axios from "axios";
import type {
  Inventory,
  InventoryPage,
  InventoryUpdateRequest,
} from "../types/Inventory";

const BASE_URL = import.meta.env.VITE_API_URL;

export const InventoryService = {
  // -> GET ENDPOINTS
  async getAllInventoriesByProductName(
    productName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<InventoryPage>(
      BASE_URL + "/api/v1/inventories/productName",
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

  async getAllInventories() {
    const response = await axios.get<Inventory[]>(
      BASE_URL + "/api/v1/inventories/",
    );

    return response.data;
  },

  async getInventoryById(id: number) {
    const response = await axios.get<Inventory>(
      BASE_URL + "/api/v1/inventories/" + id,
    );

    return response.data;
  },

  // -> PATCH ENDPOINTS
  async updateInventoryById(id: number, request: InventoryUpdateRequest) {
    const response = await axios.patch<Inventory>(
      BASE_URL + "/api/v1/inventories/" + id + "/update",
      request,
    );

    return response.data;
  },
};
