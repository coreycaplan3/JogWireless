# Corey Caplan's Makefile for Jog Wireless
# CSE 341
# Friday, May 6
# Final Project

JAVA_CLASSES = database/ChiefExecutiveDatabase.java database/ColumnTypes.java database/CustomerDatabase.java database/CustomerUsageDatabase.java database/DatabaseApi.java \
database/DatabaseInitializer.java database/PlanParser.java database/ResultSetHelper.java database/SalesClerkDatabase.java \
database/TableConstants.java \
\
forms/MainForm.java \
\
interfaces/AbstractCustomerInterface.java interfaces/BaseInterface.java interfaces/BusinessManagingInterface.java interfaces/ChiefExecutiveInterface.java \
interfaces/CustomerInStoreInterface.java interfaces/NewBusinessInterface.java interfaces/NewCustomerInterface.java interfaces/SalesClerkInterface.java \
interfaces/StreamInputInterface.java interfaces/UsePhoneInterface.java \
\
validation/FormValidation.java

RAW_CLASSES = database/ChiefExecutiveDatabase.class database/ColumnTypes.class database/CustomerDatabase.class database/CustomerUsageDatabase.class \
database/CustomerUsageDatabase\$$UsageResult.class database/DatabaseApi.class \
database/DatabaseApi\$$Database.class database/DatabaseInitializer.class database/PlanParser.class database/ResultSetHelper.class database/SalesClerkDatabase.class \
database/TableConstants.class database/TableConstants\$$Account.class database/TableConstants\$$Bill.class database/TableConstants\$$Customer.class \
database/TableConstants\$$Plans.class database/TableConstants\$$PhoneModel.class database/TableConstants\$$PhoneProduct.class database/TableConstants\$$Service.class \
\
forms/MainForm.class \
\
interfaces/AbstractCustomerInterface.class interfaces/BaseInterface.class interfaces/BusinessManagingInterface.class interfaces/ChiefExecutiveInterface.class \
interfaces/CustomerInStoreInterface.class interfaces/NewBusinessInterface.class interfaces/NewCustomerInterface.class interfaces/SalesClerkInterface.class \
interfaces/StreamInputInterface.class interfaces/StreamInputInterface\$$UsageType.class interfaces/UsePhoneInterface.class \
\
validation/FormValidation.class

cdc218.jar: classes
	jar -cvmf MANIFEST.MF cdc218.jar $(RAW_CLASSES)

classes: $(JAVA_CLASSES)
	javac $(JAVA_CLASSES)

clean:
	rm -f *~ cdc218.jar *#