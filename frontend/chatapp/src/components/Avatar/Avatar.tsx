import { useMemo } from 'react';

import { StyledAvatar, StyledOnlineIndicator } from './Avatar.styled';

export type AvatarSize = 'md' | 'lg';

type AvatarProps = {
  name: string;
  online?: boolean;
  size?: AvatarSize;
  className?: string;
};

export const Avatar = ({
  size = 'lg',
  name,
  online,
  className,
}: AvatarProps) => {
  const abbreviation = useMemo(
    () =>
      name
        .split(' ')
        .slice(0, 2)
        .reduce((acc, item) => {
          const symbol = item[0];
          return `${acc}${symbol ? symbol.toUpperCase() : ''}`;
        }, ''),
    [name]
  );

  return (
    <StyledAvatar {...{ size, className }}>
      {abbreviation}
      {online && <StyledOnlineIndicator {...{ size }} />}
    </StyledAvatar>
  );
};
