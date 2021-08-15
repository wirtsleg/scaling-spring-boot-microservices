import { useState } from 'react';

import { useStompClientContext } from 'providers';
import { useMount } from 'lib';
import { User } from 'types';

import { StyledContactList } from './ContactList.styled';
import { Contact } from './units';

export const ContactList = () => {
  const stompClient = useStompClientContext();
  const [contactsMap, setContactsMap] = useState<Map<number, User>>(new Map());

  useMount(() => {
    const subscription = stompClient.subscribe('/user/topic/contacts', msg => {
      const items = JSON.parse(msg.body);

      if (Array.isArray(items)) {
        for (const contact of items) {
          setContactsMap(new Map(contactsMap.set(contact.id, contact)));
        }
      }
    });

    return () => {
      subscription.unsubscribe();
    };
  });

  return (
    <StyledContactList>
      {[...contactsMap.values()].map(contact => (
        <Contact key={contact.id} {...contact} />
      ))}
    </StyledContactList>
  );
};
