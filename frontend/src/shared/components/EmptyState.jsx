export default function EmptyState({ icon, title, description, action }) {
    return (
        <div className="text-center py-16">
            {icon && <div className="mb-4 text-gray-400">{icon}</div>}
            <h3 className="text-xl font-semibold text-gray-900 mb-2">{title}</h3>
            {description && <p className="text-gray-600 mb-6">{description}</p>}
            {action && <div>{action}</div>}
        </div>
    );
}
