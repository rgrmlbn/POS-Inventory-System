import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CategoryService } from "../../services/CategoryService";

export function useDeleteAllCategoriesMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: () => CategoryService.deleteAllCategories(),
    onSuccess: () => {
      queryClient.removeQueries({ queryKey: ["category"] });
      queryClient.invalidateQueries({ queryKey: ["categories"] });
    },
  });

  return {
    deleteAllCategories: mutation.mutate,
    deletingAll: mutation.isPending,
    error: mutation.error,
  };
}
