package org.seqhack.intellimatch.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.seqhack.intellimatch.models.PersonalityType;

public class DBUtil {
	private static SessionFactory factory;

	/* Method to CREATE an employee in the database */
	public static Integer addPersonalityType(String fname, String lname,
			String email, String text, Double e, Double n, Double t, Double j) {
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		Session session = factory.openSession();
		Transaction tx = null;
		int id = 0;
		try {
			tx = session.beginTransaction();
			PersonalityType type = new PersonalityType();
			type.setEmail(email);
			type.setExtraverted(e);
			type.setFirstName(fname);
			type.setLastName(lname);
			type.setIntuitive(n);
			id = (Integer) session.save(type);
			tx.commit();
		} catch (HibernateException e1) {
			if (tx != null)
				tx.rollback();
			e1.printStackTrace();
		} finally {
			session.close();
		}
		factory.close();
		return id;
	}

	public static List<PersonalityType> getType() {
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		Session session = factory.openSession();
		Transaction tx = null;
		List<PersonalityType> types = new ArrayList<PersonalityType>();
		try {
			tx = session.beginTransaction();
			List personalityTypes = session.createQuery("from PersonalityType")
					.list();
			for (Iterator iterator = personalityTypes.iterator(); iterator
					.hasNext();) {
				PersonalityType type = (PersonalityType) iterator.next();
				types.add(type);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return types;
	}

}
