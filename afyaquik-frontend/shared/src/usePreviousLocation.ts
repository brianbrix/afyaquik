import { useLocation, Location } from "react-router-dom";
import { useEffect, useRef } from "react";

export const usePreviousLocation = (): Location => {
    const location = useLocation();
    const prevLocationRef = useRef<Location>(location);

    useEffect(() => {
        prevLocationRef.current = location;
    }, [location]);

    return prevLocationRef.current;
};
