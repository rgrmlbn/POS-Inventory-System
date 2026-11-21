import { useMutation, useQueryClient } from "@tanstack/react-query";
import { InventoryService } from "../../services/InventoryService";
import type { InventoryUpdateRequest } from "../../types/Inventory";

interface InventoryUpdateRequestVariables {
    id: number
    request: InventoryUpdateRequest
}

export function useUpdateInventoryMutation() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: ({ id, request }: InventoryUpdateRequestVariables) =>
      InventoryService.updateInventoryById(id, request),

    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ["inventory", id] });
      queryClient.invalidateQueries({ queryKey: ["inventories"] });
    },
  });

  return {
    updateInventory: mutation.mutate,
    updating: mutation.isPending,
    error: mutation.error,
  };
}