import { useMainPageContext } from 'pages/Main/hooks';
import { User as UserProps } from 'types';

import { StyledAvatar, StyledContact, StyledName } from './Contact.styled';

export const Contact = (props: UserProps) => {
  const { id, name, online } = props;
  const { activeContact, setActiveContact } = useMainPageContext();
  const isActive = activeContact?.id === id;

  const handleClick = () => {
    setActiveContact(props);
  };

  return (
    <StyledContact {...{ isActive }} onClick={handleClick}>
      <StyledAvatar {...{ name, online }} />
      <StyledName>{name}</StyledName>
    </StyledContact>
  );
};
