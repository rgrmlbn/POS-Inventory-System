import axios from "axios";
import { useForm } from "react-hook-form";
import { useProductsQuery } from "../../product/hooks/get/useProductsQuery";
import { useCreatePurchaseMutation } from "../hooks/useCreatePurchaseMutation";
import type { PurchaseCreateRequest } from "../types/Purchase";

export function PurchaseCreateForm() {
  const { createPurchase, creating } = useCreatePurchaseMutation();
  const {
    products,
    loading: productsLoading,
    error: productsError,
  } = useProductsQuery();

  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors },
  } = useForm<PurchaseCreateRequest>({
    defaultValues: {
      productId: 0,
      purchaseQuantity: 1,
      unitCost: 0,
      supplierName: "",
      note: "",
    },
  });

  function onSubmit(values: PurchaseCreateRequest) {
    createPurchase(
      {
        ...values,
        supplierName: values.supplierName.trim(),
        note: values.note?.trim() || "",
      },
      {
        onSuccess: () => reset(),
        onError: (error) => {
          const message = axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Failed to create purchase.")
            : "Failed to create purchase.";

          setError("root.server", { type: "server", message });
        },
      },
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <div>
        <label htmlFor="purchase-product">Product:</label>
        <select
          id="purchase-product"
          disabled={
            creating ||
            productsLoading ||
            Boolean(productsError) ||
            products.length === 0
          }
          {...register("productId", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isInteger(value) && value > 0) || "A product is required",
          })}
        >
          <option value={0}>Select a product</option>
          {products.map((product) => (
            <option key={product.id} value={product.id}>
              {product.productName}
            </option>
          ))}
        </select>
        {productsLoading && <p>Loading products...</p>}
        {productsError && <p>Failed to load products.</p>}
        {!productsLoading && !productsError && products.length === 0 && (
          <p>Create a product first.</p>
        )}
        {errors.productId && <p>{errors.productId.message}</p>}
      </div>

      <div>
        <input
          type="number"
          min="1"
          step="1"
          placeholder="Purchase quantity"
          disabled={creating}
          {...register("purchaseQuantity", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isInteger(value) && value > 0) ||
              "Quantity must be a positive whole number",
          })}
        />
        {errors.purchaseQuantity && <p>{errors.purchaseQuantity.message}</p>}
      </div>

      <div>
        <input
          type="number"
          min="0"
          step="0.01"
          placeholder="Unit cost"
          disabled={creating}
          {...register("unitCost", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isFinite(value) && value >= 0) ||
              "Enter a valid non-negative unit cost",
          })}
        />
        {errors.unitCost && <p>{errors.unitCost.message}</p>}
      </div>

      <div>
        <input
          type="text"
          placeholder="Supplier name"
          disabled={creating}
          {...register("supplierName", {
            required: "Supplier name is required",
            validate: (value) =>
              value.trim().length > 0 || "Supplier name is required",
          })}
        />
        {errors.supplierName && <p>{errors.supplierName.message}</p>}
      </div>

      <div>
        <textarea
          placeholder="Note (optional)"
          disabled={creating}
          {...register("note")}
        />
      </div>

      <button
        type="submit"
        disabled={
          creating ||
          productsLoading ||
          Boolean(productsError) ||
          products.length === 0
        }
      >
        {creating ? "Adding..." : "Add purchase"}
      </button>

      {errors.root?.server && <p>{errors.root.server.message}</p>}
    </form>
  );
}