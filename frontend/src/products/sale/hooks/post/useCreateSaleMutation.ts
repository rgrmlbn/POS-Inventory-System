import { useMutation, useQueryClient } from "@tanstack/react-query";
import { SaleService } from "../../services/SaleService";
import type { SaleCreateRequest } from "../../types/Sale";

export function useCreateSaleMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (request: SaleCreateRequest) =>
      SaleService.createSale(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["sales"] });
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });

  return {
    createSale: mutation.mutate,
    creating: mutation.isPending,
    error: mutation.error,
  };
}
