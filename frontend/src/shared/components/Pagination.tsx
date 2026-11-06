interface PaginationProps {
    page: number
    totalPages: number
    size: number
    onPageChange: (page: number) => void
    onSizeChange: (size: number) => void
}

export function Pagination({
    page,
    totalPages,
    size,
    onPageChange,
    onSizeChange,
}: PaginationProps) {
    return (
        <>
            <button
                disabled={page === 0}
                onClick={() => onPageChange(page - 1)}
            >
                Previous
            </button>

            <span> Page {page + 1} of {Math.max(totalPages, 1)} </span>

            <button
                disabled={page >= totalPages - 1}
                onClick={() => onPageChange(page + 1)}
            >
                Next
            </button>

            <select
                value={size}
                onChange={e => onSizeChange(Number(e.target.value))}
            >
                <option value={5}>5 per page</option>
                <option value={10}>10 per page</option>
                <option value={20}>20 per page</option>
            </select>
        </>
    );
}
