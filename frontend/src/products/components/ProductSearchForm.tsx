import { useForm } from "react-hook-form";

interface SearchForm {
    productName: string;
}

interface ProductSearchFormProps {
    onSearch: (productName: string) => void;
    onClear: () => void;
}

export function ProductSearchForm({ onSearch, onClear }: ProductSearchFormProps) {
    const { register, handleSubmit, reset } = useForm<SearchForm>({
        defaultValues: { productName: "" },
    });

    const submit = (data: SearchForm) => {
        onSearch(data.productName.trim());
    };

    const clear = () => {
        reset();
        onClear();
    };

    return (
        <form onSubmit={handleSubmit(submit)} role="search">
            <input
                type="text" // search if you want to use the browser's built-in search features "x" button.
                placeholder="Search by product name..."
                {...register("productName")}
            />
            <button type="submit">Search</button>
            <button type="button" onClick={clear}>Clear</button>
        </form>
    );
}