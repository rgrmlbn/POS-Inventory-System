import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CategoryService } from "../services/CategoryService";
import type { CategoryUpdateRequest } from "../types/Category";

interface UpdateCategoryVariables {
  id: number;
  request: CategoryUpdateRequest;
};

export function useUpdateCategoryMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: ({ id, request }: UpdateCategoryVariables) =>
      CategoryService.updateCategoryById(id, request),
    onSuccess: (_data, { id }) => {
      queryClient.invalidateQueries({ queryKey: ["category", id] });
      queryClient.invalidateQueries({ queryKey: ["categories"] });
    },
  });

  return {
    updateCategory: mutation.mutate,
    updating: mutation.isPending,
    error: mutation.error,
  };
}