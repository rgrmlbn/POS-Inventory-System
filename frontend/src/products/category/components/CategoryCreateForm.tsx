// components/CategoryCreateForm.tsx
import axios from "axios";
import { useForm } from "react-hook-form";
import { useCreateCategoryMutation } from "../hooks/useCreateCategoryMutation";
import type { CategoryCreateRequest } from "../types/Category";

export function CategoryCreateForm() {
  const { createCategory, creating } = useCreateCategoryMutation();

  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors },
  } = useForm<CategoryCreateRequest>({
    defaultValues: { categoryName: "" },
  });

  function onSubmit(values: CategoryCreateRequest) {
    createCategory(
      { categoryName: values.categoryName.trim() },
      {
        onSuccess: () => reset(),
        onError: (error) => {
          const message = axios.isAxiosError(error) // 1. CONDITION
            ? (error.response?.data?.message ?? "Failed to create category.") // 2. if true -- if left is null, use right
            : "Failed to create category."; // 3. if false

          // show the server error under the field
          setError("categoryName", { type: "server", message });
        },
      },
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <input
        type="text"
        placeholder="New category name"
        disabled={creating}
        {...register("categoryName", {
          required: "Category name is required",
          validate: (value) => value.trim().length > 0 || "Category name is required",
          maxLength: { value: 100, message: "Maximum 100 characters" },
        })}
      />

      <button type="submit" disabled={creating}>
        {creating ? "Adding..." : "Add category"}
      </button>

      {errors.categoryName && <p>{errors.categoryName.message}</p>}
    </form>
  );
}