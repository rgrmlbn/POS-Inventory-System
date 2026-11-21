import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useDeleteProductMutation } from "../hooks/useDeleteProductMutation";

export function ProductDeleteButton({ id }: { id: number }) {
  const { deleteProduct, deleting } = useDeleteProductMutation();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  function handleDelete() {
    setErrorMessage(null);

    deleteProduct(id, {
      onSuccess: () => {
        navigate("/products");
      },
      onError: (error) => {
        const message = axios.isAxiosError(error)
          ? (error.response?.data?.message ?? "Failed to delete product.")
          : "Failed to delete product.";

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