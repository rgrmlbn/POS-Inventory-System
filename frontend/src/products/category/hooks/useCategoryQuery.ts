import { useQuery } from "@tanstack/react-query";
import { CategoryService } from "../services/CategoryService";

export function useCategoryQuery(categoryId: number) {
  const query = useQuery({
    queryKey: ["category", categoryId],
    queryFn: () => CategoryService.getCategoryById(categoryId),
  });

  return {
    category: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
