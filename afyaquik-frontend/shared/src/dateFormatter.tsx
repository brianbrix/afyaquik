import dayjs from 'dayjs';
export default function formatDate(value: any): string {
    if (!value) return 'N/A';
    const date = new Date(value);
    if (isNaN(date.getTime())) return value;
    return dayjs(date).format('DD MMM YYYY, hh:mm A');
}
