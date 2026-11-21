import { useMutation, useQueryClient } from "@tanstack/react-query";
import { PurchaseService } from "../services/PurchaseService";
import type { PurchaseCreateRequest } from "../types/Purchase";

export function useCreatePurchaseMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (request: PurchaseCreateRequest) =>
      PurchaseService.createPurchase(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["purchases"] });
    },
  });

  return {
    createPurchase: mutation.mutate,
    creating: mutation.isPending,
    error: mutation.error,
  };
}
