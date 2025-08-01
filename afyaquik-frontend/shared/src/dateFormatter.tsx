import dayjs from 'dayjs';
export default function formatDate(value: any, isDate?:boolean): string {
    if (!value) return 'N/A';
    const date = new Date(value);
    if (isNaN(date.getTime())) return value;
    if (isDate){
        return dayjs(date).format('DD MMM YYYY');
    }
    return dayjs(date).format('DD MMM YYYY, hh:mm A');
}

export function formatForDatetimeLocal(date: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
export function formatJustDate(date: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}
