import axios from "axios";
import { useForm } from "react-hook-form";
import { useUpdateInventoryMutation } from "../hooks/patch/useUpdateInventoryMutation";
import type { Inventory, InventoryUpdateRequest } from "../types/Inventory";

interface InventoryEditFormProps {
  inventory: Inventory;
  onDone: () => void;
}

export function InventoryEditForm({ inventory, onDone }: InventoryEditFormProps) {
  const { updateInventory, updating } = useUpdateInventoryMutation();

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<InventoryUpdateRequest>({
    defaultValues: { quantity: inventory.quantity },
  });

  function onSubmit(values: InventoryUpdateRequest) {
    updateInventory(
      {
        id: inventory.id,
        request: { quantity: values.quantity },
      },
      {
        onSuccess: () => onDone(),
        onError: (error) => {
          const message = axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Failed to update inventory.")
            : "Failed to update inventory.";

          setError("root.server", { type: "server", message });
        },
      },
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <input
        type="number"
        min={0}
        step={1}
        disabled={updating}
        {...register("quantity", {
          valueAsNumber: true,
          required: "Quantity is required",
          min: { value: 0, message: "Quantity cannot be negative" },
          validate: (value) =>
            Number.isInteger(value) || "Quantity must be a whole number",
        })}
      />

      <button type="submit" disabled={updating}>
        {updating ? "Saving..." : "Save"}
      </button>
      <button type="button" onClick={onDone} disabled={updating}>
        Cancel
      </button>

      {errors.quantity && <p>{errors.quantity.message}</p>}
      {errors.root?.server && <p>{errors.root.server.message}</p>}
    </form>
  );
}
