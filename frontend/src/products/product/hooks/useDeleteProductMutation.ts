import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ProductService } from "../services/ProductService";

export function useDeleteProductMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (id: number) => ProductService.deleteProductById(id),
    onSuccess: (_, id) => {
      queryClient.removeQueries({ queryKey: ["product", id] });
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });

  return {
    deleteProduct: mutation.mutate,
    deleting: mutation.isPending,
    error: mutation.error
  };
}
