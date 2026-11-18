// components/CategoryDeleteButton.tsx
import { useState } from "react";
import axios from "axios";
import { useDeleteCategoryMutation } from "../hooks/useDeleteCategoryMutation";

interface CategoryDeleteButtonProps {
  id: number;
}

export function CategoryDeleteButton({ id }: CategoryDeleteButtonProps) {
  const { deleteCategory, deleting } = useDeleteCategoryMutation();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  function handleDelete() {
    setErrorMessage(null); // clear the old error before trying again

    deleteCategory(id, {
      onSuccess: () => {
        // UI work after a successful delete (optional)
      },
      onError: (error) => {
        const message = axios.isAxiosError(error)
          ? (error.response?.data?.message ?? "Failed to delete category.")
          : "Failed to delete category.";

        setErrorMessage(message);
      },
    });
  }

  return (
    <>
      <button onClick={handleDelete} disabled={deleting}>
        {deleting ? "Deleting..." : "Delete"}
      </button>

      {errorMessage && <p>{errorMessage}</p>}
    </>
  );
}