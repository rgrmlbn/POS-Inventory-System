import { useProductCountQuery } from "../hooks/useProductCountQuery";

export function Dashboard() {
    const { data, isLoading, isError } = useProductCountQuery();

    return (
        <div>
            <h1>Dashboard</h1>
            {isLoading && <p>Loading product count...</p>}
            {isError && <p>Failed to load product count.</p>}
            {data && <p>Product count: {data.productCount}</p>}
        </div>
    );
}