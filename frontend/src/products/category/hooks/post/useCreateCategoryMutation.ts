import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CategoryService } from "../../services/CategoryService";
import type { CategoryCreateRequest } from "../../types/Category";

export function useCreateCategoryMutation() {
    
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (request: CategoryCreateRequest) =>
      CategoryService.createCategory(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] });
    },
  });

  return {
    createCategory: mutation.mutate,
    creating: mutation.isPending,
    error: mutation.error,
  };
}