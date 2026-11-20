import { CategoryService } from "../services/CategoryService";
import { keepPreviousData, useQuery } from "@tanstack/react-query";

interface CategoriesQueryParams {
  categoryName: string;
  page: number;
  size: number;
};

export function useCategoriesQuery({
  categoryName,
  page,
  size,
}: CategoriesQueryParams) {
  const query = useQuery({
    queryKey: ["categories", { categoryName, page, size }],
    queryFn: () =>
      CategoryService.getAllCategoriesByCategoryName(categoryName, page, size),
    placeholderData: keepPreviousData,
  });

  return {
    categories: query.data?.content ?? [],
    totalPages: query.data?.totalPages ?? 0,
    loading: query.isLoading,
    error: query.error,
  };
}