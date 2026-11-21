// components/CategoryDeleteButton.tsx
import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useDeleteCategoryMutation } from "../hooks/useDeleteCategoryMutation";

export function CategoryDeleteButton({ id }: { id: number }) {
  const { deleteCategory, deleting } = useDeleteCategoryMutation();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  function handleDelete() {
    setErrorMessage(null); // clear the old error before trying again

    deleteCategory(id, {
      onSuccess: () => {
        navigate("/categories");
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
