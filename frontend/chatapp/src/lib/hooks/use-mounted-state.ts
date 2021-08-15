import { useCallback, useEffect, useRef } from 'react';

export const useMountedState = () => {
  const mountedRef = useRef(false);

  useEffect(() => {
    mountedRef.current = true;

    return () => {
      mountedRef.current = false;
    };
  });

  return useCallback(() => mountedRef.current, []);
};
