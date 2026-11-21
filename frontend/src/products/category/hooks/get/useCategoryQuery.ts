import { useQuery } from "@tanstack/react-query";
import { CategoryService } from "../../services/CategoryService";

export function useCategoryQuery(id: number) {
  const query = useQuery({
    queryKey: ["category", id],
    queryFn: () => CategoryService.getCategoryById(id),
  });

  return {
    category: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
