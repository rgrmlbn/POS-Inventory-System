import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ProductService } from "../../services/ProductService";
import type { ProductCreateSubmission } from "../../types/Product";

export function useCreateProductMutation() {
    
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (request: ProductCreateSubmission) =>
      ProductService.createProduct(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });

  return {
    createProduct: mutation.mutate,
    creating: mutation.isPending,
    error: mutation.error,
  };
}