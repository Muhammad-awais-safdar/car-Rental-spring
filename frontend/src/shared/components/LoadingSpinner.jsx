export default function LoadingSpinner({ size = 'md', color = '#EF4444' }) {
    const sizes = {
        sm: 'h-6 w-6',
        md: 'h-12 w-12',
        lg: 'h-16 w-16',
    };

    return (
        <div className="flex justify-center items-center py-12">
            <div
                className={`animate-spin rounded-full border-b-2 ${sizes[size]}`}
                style={{ borderColor: color }}
            ></div>
        </div>
    );
}
