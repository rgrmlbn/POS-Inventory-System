import { useQuery } from "@tanstack/react-query";
import { CategoryService } from "../services/CategoryService";

export function useCategoriesQuery() {
  const query = useQuery({
    queryKey: ["categories", "all"],
    queryFn: () => CategoryService.getAllCategories(),
  });

  return {
    categories: query.data ?? [],
    loading: query.isLoading,
    error: query.error,
  };
}