import axios from "axios";
import { useFieldArray, useForm } from "react-hook-form";
import { useProductsQuery } from "../../product/hooks/get/useProductsQuery";
import { useCreateSaleMutation } from "../hooks/post/useCreateSaleMutation";
import type { SaleCreateRequest } from "../types/Sale";

export function SaleCreateForm() {
  const { createSale, creating } = useCreateSaleMutation();
  const {
    products,
    loading: productsLoading,
    error: productsError,
  } = useProductsQuery();

  const {
    register,
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors },
  } = useForm<SaleCreateRequest>({
    defaultValues: {
      items: [{ productId: 0, quantity: 1 }],
      discount: 0,
      paymentMethod: "CASH",
      amountPaid: 0,
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: "items",
  });

  function onSubmit(values: SaleCreateRequest) {
    createSale(
      {
        items: values.items,
        discount: values.discount ?? 0,
        paymentMethod: values.paymentMethod,
        amountPaid: values.amountPaid,
      },
      {
        onSuccess: () => reset(),
        onError: (error) => {
          const message = axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Failed to create sale.")
            : "Failed to create sale.";

          setError("root.server", { type: "server", message });
        },
      },
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      {fields.map((field, index) => (
        <div key={field.id}>
          <label htmlFor={`sale-product-${index}`}>Product:</label>
          <select
            id={`sale-product-${index}`}
            disabled={
              creating ||
              productsLoading ||
              Boolean(productsError) ||
              products.length === 0
            }
            {...register(`items.${index}.productId`, {
              valueAsNumber: true,
              validate: (value) =>
                (Number.isInteger(value) && value > 0) ||
                "A product is required",
            })}
          >
            <option value={0}>Select a product</option>
            {products.map((product) => (
              <option key={product.id} value={product.id}>
                {product.productName}
              </option>
            ))}
          </select>
          {errors.items?.[index]?.productId && (
            <p>{errors.items[index]?.productId?.message}</p>
          )}

          <input
            type="number"
            min="1"
            step="1"
            placeholder="Sale quantity"
            disabled={creating}
            {...register(`items.${index}.quantity`, {
              valueAsNumber: true,
              validate: (value) =>
                (Number.isInteger(value) && value > 0) ||
                "Quantity must be a positive whole number",
            })}
          />
          {errors.items?.[index]?.quantity && (
            <p>{errors.items[index]?.quantity?.message}</p>
          )}

          <button
            type="button"
            disabled={creating || fields.length === 1}
            onClick={() => remove(index)}
          >
            Remove item
          </button>
        </div>
      ))}

      <button
        type="button"
        disabled={creating}
        onClick={() => append({ productId: 0, quantity: 1 })}
      >
        Add item
      </button>

      {productsLoading && <p>Loading products...</p>}
      {productsError && <p>Failed to load products.</p>}
      {!productsLoading && !productsError && products.length === 0 && (
        <p>Create a product first.</p>
      )}

      <div>
        <input
          type="number"
          min="0"
          step="0.01"
          placeholder="Discount (optional)"
          disabled={creating}
          {...register("discount", {
            valueAsNumber: true,
            validate: (value) =>
              value === undefined ||
              (Number.isFinite(value) && value >= 0) ||
              "Discount must be a non-negative amount",
          })}
        />
        {errors.discount && <p>{errors.discount.message}</p>}
      </div>

      <div>
        <label htmlFor="sale-payment-method">Payment method:</label>
        <select
          id="sale-payment-method"
          disabled={creating}
          {...register("paymentMethod", {
            required: "Payment method is required",
          })}
        >
          <option value="CASH">Cash</option>
          <option value="CARD">Card</option>
          <option value="EWALLET">E-wallet</option>
        </select>
        {errors.paymentMethod && <p>{errors.paymentMethod.message}</p>}
      </div>

      <div>
        <input
          type="number"
          min="0"
          step="0.01"
          placeholder="Amount paid"
          disabled={creating}
          {...register("amountPaid", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isFinite(value) && value >= 0) ||
              "Enter a valid non-negative amount paid",
          })}
        />
        {errors.amountPaid && <p>{errors.amountPaid.message}</p>}
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
        {creating ? "Adding..." : "Add sale"}
      </button>

      {errors.root?.server && <p>{errors.root.server.message}</p>}
    </form>
  );
}
