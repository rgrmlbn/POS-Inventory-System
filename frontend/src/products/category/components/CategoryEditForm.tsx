// components/CategoryEditForm.tsx
import axios from "axios";
import { useForm } from "react-hook-form";
import { useUpdateCategoryMutation } from "../hooks/useUpdateCategoryMutation";
import type { Category, CategoryCreateRequest } from "../types/Category";

interface CategoryEditFormProps {
  category: Category
  onDone: () => void // close edit mode
}

export function CategoryEditForm({ category, onDone }: CategoryEditFormProps) {
  const { updateCategory, updating } = useUpdateCategoryMutation();

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<CategoryCreateRequest>({
    defaultValues: { categoryName: category.categoryName }, // pre-fill the input
  });

  function onSubmit(values: CategoryCreateRequest) {
    updateCategory(
      {
        id: category.id,
        request: { categoryName: values.categoryName.trim() },
      },
      {
        onSuccess: () => onDone(),
        onError: (error) => {
          const message = axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Failed to update category.")
            : "Failed to update category.";

          setError("root.server", { type: "server", message });
        },
      },
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <input
        type="text"
        disabled={updating}
        {...register("categoryName", {
          required: "Category name is required",
          validate: (value) => value.trim().length > 0 || "Category name is required",
          maxLength: { value: 100, message: "Maximum 100 characters" },
        })}
      />

      <button type="submit" disabled={updating}>
        {updating ? "Saving..." : "Save"}
      </button>
      <button type="button" onClick={onDone} disabled={updating}>
        Cancel
      </button>

      {errors.categoryName && <p>{errors.categoryName.message}</p>}
      {errors.root?.server && <p>{errors.root.server.message}</p>}
    </form>
  );
}
