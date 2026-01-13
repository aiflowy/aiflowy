export const uuid = () => {
  const getRandomByte = (): number => {
    // prefer secure crypto if available, support legacy msCrypto, otherwise fallback
    if (
      typeof globalThis !== 'undefined' &&
      typeof (globalThis as any).crypto === 'object' &&
      typeof (globalThis as any).crypto.getRandomValues === 'function'
    ) {
      return (globalThis as any).crypto.getRandomValues(new Uint8Array(1))[0];
    }
    if (
      typeof (globalThis as any).msCrypto === 'object' &&
      typeof (globalThis as any).msCrypto.getRandomValues === 'function'
    ) {
      return (globalThis as any).msCrypto.getRandomValues(new Uint8Array(1))[0];
    }
    return Math.floor(Math.random() * 256);
  };

  return '10000000-1000-4000-8000-100000000000'.replaceAll(/[018]/g, (c: any) =>
    (Number(c) ^ (getRandomByte() & (15 >> (Number(c) / 4)))).toString(16),
  );
};
