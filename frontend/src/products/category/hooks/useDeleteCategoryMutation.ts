import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CategoryService } from "../services/CategoryService";

export function useDeleteCategoryMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (id: number) => CategoryService.deleteCategoryById(id),
    onSuccess: (_, id) => {
      queryClient.removeQueries({ queryKey: ["category", id] });
      queryClient.invalidateQueries({ queryKey: ["categories"] });
    },
  });

  return {
    deleteCategory: mutation.mutate,
    deleting: mutation.isPending,
    error: mutation.error
  };
}