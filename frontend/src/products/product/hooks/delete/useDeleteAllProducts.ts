import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ProductService } from "../../services/ProductService";

export function useDeleteAllCategoriesMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: () => ProductService.deleteAllProducts(),
    onSuccess: () => {
      queryClient.removeQueries({ queryKey: ["product"] });
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });

  return {
    deleteAllProducts: mutation.mutate,
    deletingAll: mutation.isPending,
    error: mutation.error,
  };
}
