import { useState } from 'react';

export default function AvailabilityCalendar({ onDateSelect, minDate, blockedDates = [] }) {
    const [currentMonth, setCurrentMonth] = useState(new Date());
    const [selectedStart, setSelectedStart] = useState(null);
    const [selectedEnd, setSelectedEnd] = useState(null);

    const getDaysInMonth = (date) => {
        const year = date.getFullYear();
        const month = date.getMonth();
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const daysInMonth = lastDay.getDate();
        const startingDayOfWeek = firstDay.getDay();

        return { daysInMonth, startingDayOfWeek };
    };

    const isDateBlocked = (date) => {
        return blockedDates.some(blocked =>
            date.toDateString() === new Date(blocked).toDateString()
        );
    };

    const isDateInPast = (date) => {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return date < today;
    };

    const handleDateClick = (day) => {
        const clickedDate = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), day);

        if (isDateInPast(clickedDate) || isDateBlocked(clickedDate)) return;

        if (!selectedStart || (selectedStart && selectedEnd)) {
            setSelectedStart(clickedDate);
            setSelectedEnd(null);
        } else {
            if (clickedDate < selectedStart) {
                setSelectedEnd(selectedStart);
                setSelectedStart(clickedDate);
            } else {
                setSelectedEnd(clickedDate);
            }

            if (onDateSelect) {
                onDateSelect({
                    startDate: selectedStart < clickedDate ? selectedStart : clickedDate,
                    endDate: selectedStart < clickedDate ? clickedDate : selectedStart,
                });
            }
        }
    };

    const isDateInRange = (day) => {
        if (!selectedStart || !selectedEnd) return false;
        const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), day);
        return date >= selectedStart && date <= selectedEnd;
    };

    const isDateSelected = (day) => {
        const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), day);
        return (selectedStart && date.toDateString() === selectedStart.toDateString()) ||
            (selectedEnd && date.toDateString() === selectedEnd.toDateString());
    };

    const { daysInMonth, startingDayOfWeek } = getDaysInMonth(currentMonth);

    const prevMonth = () => {
        setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1));
    };

    const nextMonth = () => {
        setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1));
    };

    return (
        <div className="bg-white rounded-lg shadow-md p-4">
            {/* Header */}
            <div className="flex justify-between items-center mb-4">
                <button onClick={prevMonth} className="p-2 hover:bg-gray-100 rounded-full">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                    </svg>
                </button>
                <h3 className="text-lg font-semibold">
                    {currentMonth.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
                </h3>
                <button onClick={nextMonth} className="p-2 hover:bg-gray-100 rounded-full">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                </button>
            </div>

            {/* Weekday Headers */}
            <div className="grid grid-cols-7 gap-1 mb-2">
                {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
                    <div key={day} className="text-center text-sm font-medium text-gray-600 py-2">
                        {day}
                    </div>
                ))}
            </div>

            {/* Calendar Days */}
            <div className="grid grid-cols-7 gap-1">
                {/* Empty cells for days before month starts */}
                {Array.from({ length: startingDayOfWeek }).map((_, i) => (
                    <div key={`empty-${i}`} className="aspect-square" />
                ))}

                {/* Actual days */}
                {Array.from({ length: daysInMonth }).map((_, i) => {
                    const day = i + 1;
                    const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), day);
                    const isPast = isDateInPast(date);
                    const isBlocked = isDateBlocked(date);
                    const inRange = isDateInRange(day);
                    const selected = isDateSelected(day);

                    return (
                        <button
                            key={day}
                            onClick={() => handleDateClick(day)}
                            disabled={isPast || isBlocked}
                            className={`aspect-square flex items-center justify-center rounded-lg text-sm transition-colors ${isPast || isBlocked
                                    ? 'text-gray-300 cursor-not-allowed'
                                    : selected
                                        ? 'bg-[#EF4444] text-white font-bold'
                                        : inRange
                                            ? 'bg-red-100 text-gray-900'
                                            : 'hover:bg-gray-100 text-gray-900'
                                }`}
                        >
                            {day}
                        </button>
                    );
                })}
            </div>

            {/* Legend */}
            <div className="mt-4 flex gap-4 text-xs text-gray-600">
                <div className="flex items-center gap-1">
                    <div className="w-4 h-4 bg-[#EF4444] rounded"></div>
                    <span>Selected</span>
                </div>
                <div className="flex items-center gap-1">
                    <div className="w-4 h-4 bg-red-100 rounded"></div>
                    <span>In Range</span>
                </div>
                <div className="flex items-center gap-1">
                    <div className="w-4 h-4 bg-gray-200 rounded"></div>
                    <span>Unavailable</span>
                </div>
            </div>
        </div>
    );
}
