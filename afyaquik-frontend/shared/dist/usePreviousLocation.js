"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.usePreviousLocation = void 0;
const react_router_dom_1 = require("react-router-dom");
const react_1 = require("react");
const usePreviousLocation = () => {
    const location = (0, react_router_dom_1.useLocation)();
    const prevLocationRef = (0, react_1.useRef)(location);
    (0, react_1.useEffect)(() => {
        prevLocationRef.current = location;
    }, [location]);
    return prevLocationRef.current;
};
exports.usePreviousLocation = usePreviousLocation;
