import axios from "axios";
import type {
  Category,
  CategoryPage,
  CategoryCreateRequest,
  CategoryUpdateRequest,
} from "../types/Category";

const BASE_URL = import.meta.env.VITE_API_URL;

export const CategoryService = {
  // -> GET ENDPOINTS
  async getAllCategoriesByCategoryName(
    categoryName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<CategoryPage>(
      BASE_URL + "/api/v1/categories/categoryName",
      {
        params: {
          categoryName,
          page,
          size,
        },
      },
    );

    return response.data;
  },

  async getAllCategories() {
    const response = await axios.get<Category[]>(
      BASE_URL + "/api/v1/categories",
    );

    return response.data;
  },

  async getCategoryById(id: number) {
    const response = await axios.get<Category>(
      BASE_URL + "/api/v1/categories/" + id,
    );

    return response.data;
  },

  // -> POST ENDPOINTS
  async createCategory(request: CategoryCreateRequest) {
    const response = await axios.post<Category>(
      BASE_URL + "/api/v1/categories/create",
      request,
    );
    return response.data;
  },

  // -> PATCH ENDPOINTS
  async updateCategoryById(id: number, request: CategoryUpdateRequest) {
    const response = await axios.patch<Category>(
      BASE_URL + "/api/v1/categories/" + id + "/update",
      request,
    );
    return response.data;
  },

  // -> DELETE ENDPOINTS
  async deleteCategoryById(id: number) {
    await axios.delete(BASE_URL + "/api/v1/categories/" + id + "/delete");
  },

  async deleteAllCategories() {
    await axios.delete(BASE_URL + "/api/v1/categories/delete-all");
  },
};
