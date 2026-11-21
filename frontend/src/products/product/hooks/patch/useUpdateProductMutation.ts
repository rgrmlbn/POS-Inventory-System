import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ProductService } from "../../services/ProductService";
import type { ProductUpdateRequest } from "../../types/Product";

interface ProductUpdateRequestVariables {
  id: number;
  request: ProductUpdateRequest;
}

export function useUpdateProductMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: ({ id, request }: ProductUpdateRequestVariables) =>
      ProductService.updateProductById(id, request),

    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ["product", id] });
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });

  return {
    updateProduct: mutation.mutate,
    updating: mutation.isPending,
    error: mutation.error,
  };
}
