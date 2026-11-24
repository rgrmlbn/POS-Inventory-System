import axios from "axios";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { useCategoriesQuery } from "../../category/hooks/get/useCategoriesQuery";
import { useCreateProductMutation } from "../hooks/post/useCreateProductMutation";
import type { ProductCreateFormValues } from "../types/Product";

export function ProductCreateForm() {
  const { createProduct, creating } = useCreateProductMutation();
  const [selectedImage, setSelectedImage] = useState<File | undefined>();

  const {
    categories,
    loading: categoriesLoading,
    error: categoriesError,
  } = useCategoriesQuery();

  const {
    register,
    handleSubmit,
    reset,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm<ProductCreateFormValues>({
    defaultValues: {
      productName: "",
      sku: "",
      barcode: "",
      categoryId: 0,
      costPrice: 0,
      sellingPrice: 0,
      reorderLevel: 0,
    },
  });

  function onSubmit(values: ProductCreateFormValues) {
    if (!selectedImage) {
      setError("root.server", {
        type: "validate",
        message: "A product image is required.",
      });
      return;
    }
    if (selectedImage.size > 5 * 1024 * 1024) {
      setError("root.server", {
        type: "validate",
        message: "Image must be 5 MB or smaller.",
      });
      return;
    }
    if (
      !["image/jpeg", "image/png", "image/gif", "image/webp"].includes(
        selectedImage.type,
      )
    ) {
      setError("root.server", {
        type: "validate",
        message: "Upload a JPEG, PNG, GIF, or WebP image.",
      });
      return;
    }

    createProduct(
      {
        ...values,
        productName: values.productName.trim(),
        sku: values.sku.trim(),
        barcode: values.barcode.trim(),
        image: selectedImage,
      },
      {
        onSuccess: () => {
          reset();
          setSelectedImage(undefined);
        },
        onError: (error) => {
          const message = axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Failed to create product.")
            : "Failed to create product.";
          setError("root.server", { type: "server", message });
        },
      },
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <div>
        <input
          type="text"
          placeholder="Product name"
          disabled={creating}
          {...register("productName", {
            required: "Product name is required",
            validate: (value) =>
              value.trim().length >= 3 ||
              "Product name must be at least 3 characters",
            maxLength: { value: 100, message: "Maximum 100 characters" },
          })}
        />
        {errors.productName && <p>{errors.productName.message}</p>}
      </div>

      <div>
        <input
          type="text"
          placeholder="SKU"
          disabled={creating}
          {...register("sku", {
            required: "SKU is required",
            validate: (value) =>
              value.trim().length >= 3 || "SKU must be at least 3 characters",
            maxLength: { value: 50, message: "Maximum 50 characters" },
          })}
        />
        {errors.sku && <p>{errors.sku.message}</p>}
      </div>

      <div>
        <input
          type="text"
          placeholder="Barcode"
          disabled={creating}
          {...register("barcode", {
            required: "Barcode is required",
            validate: (value) =>
              value.trim().length >= 3 ||
              "Barcode must be at least 3 characters",
            maxLength: { value: 100, message: "Maximum 100 characters" },
          })}
        />
        {errors.barcode && <p>{errors.barcode.message}</p>}
      </div>

      <div>
        <label>Category:</label>
        <select
          disabled={creating || categoriesLoading || Boolean(categoriesError)}
          {...register("categoryId", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isInteger(value) && value > 0) ||
              "A category is required",
          })}
        >
          <option value={0}>Select a category</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.categoryName}
            </option>
          ))}
        </select>
        {categoriesLoading && <p>Loading categories...</p>}
        {categoriesError && <p>Failed to load categories.</p>}
        {!categoriesLoading && !categoriesError && categories.length === 0 && (
          <p>Create a category first.</p>
        )}
        {errors.categoryId && <p>{errors.categoryId.message}</p>}
      </div>

      <div>
        <input
          type="number"
          min="0"
          step="0.01"
          placeholder="Cost price"
          disabled={creating}
          {...register("costPrice", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isFinite(value) && value >= 0) ||
              "Enter a valid non-negative cost price",
          })}
        />
        {errors.costPrice && <p>{errors.costPrice.message}</p>}
      </div>

      <div>
        <input
          type="number"
          min="0"
          step="0.01"
          placeholder="Selling price"
          disabled={creating}
          {...register("sellingPrice", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isFinite(value) && value >= 0) ||
              "Enter a valid non-negative selling price",
          })}
        />
        {errors.sellingPrice && <p>{errors.sellingPrice.message}</p>}
      </div>

      <div>
        <input
          type="number"
          min="0"
          step="1"
          placeholder="Reorder level"
          disabled={creating}
          {...register("reorderLevel", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isInteger(value) && value >= 0) ||
              "Reorder level must be a non-negative whole number",
          })}
        />
        {errors.reorderLevel && <p>{errors.reorderLevel.message}</p>}
      </div>

      <div>
        <label htmlFor="product-image">Product image:</label>
        <input
          id="product-image"
          type="file"
          accept="image/jpeg,image/png,image/gif,image/webp"
          disabled={creating}
          onChange={(event) => {
            const [file] = event.target.files ?? []; // value ?? null
            setSelectedImage(file);
            clearErrors("root.server");
          }}
        />
        <p>Required. JPEG, PNG, GIF, or WebP; maximum 5 MB.</p>
      </div>

      <button
        type="submit"
        disabled={
          creating ||
          categoriesLoading ||
          Boolean(categoriesError) ||
          categories.length === 0
        }
      >
        {creating ? "Adding..." : "Add product"}
      </button>

      {errors.root?.server && <p>{errors.root.server.message}</p>}
    </form>
  );
}