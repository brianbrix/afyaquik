import React from 'react';

export function formatCurrency(value: any) {
    if (typeof value === 'number') {
        return value.toLocaleString('en-US', { style: 'currency', currency: 'KES' });
    }
    return value;
}

export function formatNumber(value: any) {
    if (typeof value === 'number') {
        return value.toLocaleString('en-US');
    }
    return value;
}
export function formatPercentage(value: any) {
    if (typeof value === 'number') {
        return `${value.toFixed(2)}%`;
    }
    return value;
}

export function formatWysiwyg(value: string) {
    return React.createElement('div', { dangerouslySetInnerHTML: { __html: value } });
}
