import axios from "axios";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { useUpdateProductMutation } from "../hooks/patch/useUpdateProductMutation";
import type { Product, ProductCreateFormValues } from "../types/Product";
import { useCategoriesQuery } from "../../category/hooks/get/useCategoriesQuery";

interface ProductEditFormProps {
  product: Product;
  onDone: () => void;
}

export function ProductEditForm({ product, onDone }: ProductEditFormProps) {
  const { updateProduct, updating } = useUpdateProductMutation();
  const [selectedImage, setSelectedImage] = useState<File | undefined>();

  const {
    categories,
    loading: categoriesLoading,
    error: categoriesError,
  } = useCategoriesQuery();

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<ProductCreateFormValues>({
    defaultValues: {
      productName: product.productName,
      sku: product.sku,
      barcode: product.barcode,
      categoryId: product.categoryId,
      costPrice: product.costPrice,
      sellingPrice: product.sellingPrice,
      reorderLevel: product.reorderLevel,
    }, // pre-fill the input
  });

  function onSubmit(values: ProductCreateFormValues) {
    if (selectedImage && selectedImage.size > 5 * 1024 * 1024) {
      setError("root.server", {
        type: "validate",
        message: "Image must be 5 MB or smaller.",
      });
      return;
    }
    if (
      selectedImage &&
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

    updateProduct(
      {
        id: product.id,
        request: {
          ...values,
          productName: values.productName.trim(),
          sku: values.sku.trim(),
          barcode: values.barcode.trim(),
          image: selectedImage,
        },
      },
      {
        onSuccess: () => onDone(),
        onError: (error) => {
          const message = axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Failed to update product.")
            : "Failed to update product.";

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
          disabled={updating}
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
          disabled={updating}
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
          disabled={updating}
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
          disabled={updating || categoriesLoading || Boolean(categoriesError)}
          {...register("categoryId", {
            valueAsNumber: true,
            validate: (value) =>
              (Number.isInteger(value) && value > 0) ||
              "A category is required",
          })}
        >
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
          disabled={updating}
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
          disabled={updating}
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
          disabled={updating}
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
        <label htmlFor={`product-image-${product.id}`}>Product image:</label>
        {product.imageUrl && (
          <div>
            <img
              src={
                selectedImage
                  ? URL.createObjectURL(selectedImage)
                  : product.imageUrl
              }
              alt={product.productName}
              width={120}
              height={120}
            />
          </div>
        )}
        <input
          id={`product-image-${product.id}`}
          type="file"
          accept="image/jpeg,image/png,image/gif,image/webp"
          disabled={updating}
          onChange={(event) => {
            const [file] = event.target.files ?? []; // value ?? null
            setSelectedImage(file);
          }}
        />
        <p>Optional. JPEG, PNG, GIF, or WebP; maximum 5 MB.</p>
      </div>

      <button type="submit" disabled={updating}>
        {updating ? "Saving..." : "Save"}
      </button>
      <button type="button" onClick={onDone} disabled={updating}>
        Cancel
      </button>
      {errors.root?.server && <p>{errors.root.server.message}</p>}
    </form>
  );
}
