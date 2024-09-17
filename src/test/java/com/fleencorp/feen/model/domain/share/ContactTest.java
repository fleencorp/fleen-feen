package com.fleencorp.feen.model.domain.share;

import com.fleencorp.feen.constant.share.ContactType;
import com.fleencorp.feen.model.domain.user.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContactTest {

  @Test
  void create_empty_contact() {
    // GIVEN
    final Contact contact = new Contact();

    // ASSERT
    assertNotNull(contact);
  }

  //ensure contact id is not empty
  @Test
  void create_empty_contact_null() {
    // GIVEN
    final Contact contact = null;

    // ASSERT
    assertNull(contact);
  }

  @Test
  void create_contact_without_id() {
    // GIVEN
    final Contact contact = new Contact();

    // ASSERT
    assertNull(contact.getContactId());
  }

  @Test
  void create_contact_with_id() {
    // GIVEN
    final Contact contact = new Contact();
    contact.setContactId(1L);

    // ASSERT
    assertEquals(1L, contact.getContactId());
  }

  @Test
  void ensure_contact_id_are_equal() {
    // GIVEN
    final long contactId = 1L;
    final Contact contact1 = new Contact();
    contact1.setContactId(1L);

    // ASSERT
    assertNotNull(contact1);
    assertEquals(contactId, contact1.getContactId());
  }

  @Test
  void ensure_contact_id_are_not_equal() {
    // GIVEN
    final long contactId = 1L;
    final Contact contact1 = new Contact();
    contact1.setContactId(2L);

    // ASSERT
    assertNotNull(contact1);
    assertNotEquals(contactId, contact1.getContactId());
  }

  @Test
  void create_contact_without_contactType() {
    // GIVEN
    final Contact contact = new Contact();

    // ASSERT
    assertNull(contact.getContactType());
  }

  @Test
  void create_contact_with_contactType() {
    // GIVEN
    final Contact contact = new Contact();
    contact.setContactType(ContactType.FACEBOOK);

    // ASSERT
    assertEquals(ContactType.FACEBOOK, contact.getContactType());
  }

  @Test
  void ensure_contact_type_are_equal() {
    // GIVEN
    ContactType contactType = ContactType.FACEBOOK;
    final Contact contact1 = new Contact();
    contact1.setContactType(contactType);

    // ASSERT
    assertNotNull(contact1);
    assertEquals(contactType, contact1.getContactType());
  }

  @Test
  void ensure_contact_type_are_not_equal() {
    // GIVEN
    ContactType contactType1 = ContactType.FACEBOOK;
    final Contact contact1 = new Contact();

    final ContactType contactType2 = ContactType.INSTAGRAM;
    contact1.setContactType(contactType2);

    // ASSERT
    assertNotNull(contact1);
    assertNotEquals(contactType1, contact1.getContactType());
  }

  @Test
  void create_contact_without_contact() {
    // GIVEN
    final Contact contact = new Contact();

    // ASSERT
    assertNull(contact.getContact());
  }

  @Test
  void create_contact_with_contact() {
    // GIVEN
    final Contact contact = new Contact();
    final String contactString = "contact";
    contact.setContact(contactString);

    // ASSERT
    assertEquals(contactString, contact.getContact());
  }

  @Test
  void ensure_contact_are_equal() {
    // GIVEN
    String contact = "contact";
    final Contact contact1 = new Contact();
    contact1.setContact(contact);

    // ASSERT
    assertNotNull(contact1);
    assertEquals(contact, contact1.getContact());
  }

  @Test
  void ensure_contact_are_not_equal() {
    // GIVEN
    String contact1 = "contact1";
    final Contact contact = new Contact();

    String contact2 = "contact2";
    contact.setContact(contact1);

    // ASSERT
    assertNotNull(contact);
    assertNotEquals(contact2, contact.getContact());
  }

  @Test
  void create_contact_without_owner() {
    // GIVEN
    final Contact contact = new Contact();

    // ASSERT
    assertNull(contact.getOwner());
  }

  @Test
  void create_contact_with_owner() {
    // GIVEN
    final Contact contact = new Contact();
    Member member = new Member();
    contact.setOwner(member);

    // ASSERT
    assertEquals(member, contact.getOwner());
  }

  @Test
  void ensure_owner_are_equal() {
    // GIVEN
    Member owner = new Member();
    final Contact contact1 = new Contact();
    contact1.setOwner(owner);

    // ASSERT
    assertNotNull(contact1);
    assertEquals(owner, contact1.getOwner());
  }

  @Test
  void ensure_owner_are_not_equal() {
    // GIVEN
    Member owner1 = new Member();
    final Contact contact1 = new Contact();
    contact1.setOwner(owner1);

    Member owner2 = new Member();

    // ASSERT
    assertNotNull(contact1);
    assertNotEquals(owner2, contact1.getOwner());
  }
}