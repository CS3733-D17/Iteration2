package com.slackers.inc.Controllers;

import com.slackers.inc.Controllers.Filters.ExactFilter;
import com.slackers.inc.Controllers.Filters.Filter;
import com.slackers.inc.Controllers.Filters.RangeFilter;
import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import java.sql.Date;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by paluro on 3/31/17.
 */
public class UsEmployeeController {
    private UsEmployee employee;
    private LabelApplicationController formController;


    //constructor
    // controller for anything USEmployee related. allows a cleaner flow of code to be used and take advantage of other controllers
    public UsEmployeeController(UsEmployee employee, LabelApplicationController formController){
        this.employee = employee;
        this.formController=formController;
    }

    public UsEmployeeController(UsEmployee employee){
        this.employee = employee;
    }

    //methods:

   //allows a USEmployee to pull an application from the database for viewing
    public boolean pullNewApplications(){
        //stub
        return true;
    }

    //allows a USEmployee to accept an application
    public boolean acceptApplicaton(Date experationDate){

        try {
            formController.approveApplication(this.employee, experationDate);
        } catch (SQLException e) {
            return false;
        }
        return true;

        //application.setApplicationApproval(app);
    }
//allows a USEmployee to reject an application back to the manufacturer
    public boolean rejectApplication(LabelApplication application){

        try {
            formController.rejectApplication(this.employee);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    //allows a USEmployee to send a application back to the manufacturer for revisions
    public boolean sendForRevision(LabelApplication application, UsEmployee employee){
        try {
            formController.sendForCorrections(this.employee);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    //allows a USEmployee to send the application to another USEmployee for secondary review
    public boolean sendForSecondOpinion(LabelApplication application,UsEmployee employee){
        try {
            formController.setNewReviewer(employee);
        } catch (SQLException e) {
            return false;
        }
        return true;

    }
    
    public boolean refresh()
    {
        try {
            return new AccountController(this.employee).reload();
        } catch (SQLException ex) {
            Logger.getLogger(ManufacturerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public UsEmployee getEmployee() {
        return employee;
    }

    public LabelApplicationController getFormController() {
        return formController;
    }

    public static void fillApplicationList(UsEmployee employee)
    {
        
        if (employee.getApplications().isEmpty())
        {
            System.out.println("GET NEW APPLICATIONS");
            LabelApplication target = new LabelApplication();
            LabelApplication target2 = new LabelApplication();
            target.setStatus(LabelApplication.ApplicationStatus.SUBMITTED);
            target2.setStatus(LabelApplication.ApplicationStatus.SUBMITTED_FOR_REVIEW);
            target2.setReviewer(employee);
            try {
                //List<LabelApplication> forms = DerbyConnection.getInstance().getAllEntites_Typed(target, "status");
                //List<LabelApplication> forms2 = DerbyConnection.getInstance().getAllEntites_Typed(target, "status", "reviewer");

                List<Filter> filters = new LinkedList<>();
                filters.add(new ExactFilter(){
                    @Override
                    public Object getValue() {
                        return  LabelApplication.ApplicationStatus.SUBMITTED;
                    }

                    @Override
                    public String getColumn() {
                        return "status";
                    }
                });
                List<Filter> filters2 = new LinkedList<>();
                filters2.add(new ExactFilter(){
                    @Override
                    public Object getValue() {
                        return LabelApplication.ApplicationStatus.SUBMITTED_FOR_REVIEW;
                    }

                    @Override
                    public String getColumn() {
                        return "status";
                    }
                });
                filters2.add(new ExactFilter(){
                    @Override
                    public Object getValue() {
                        return employee.getEmail();
                    }

                    @Override
                    public String getColumn() {
                        return "reviewer";
                    }
                });
                List<LabelApplication> forms = DerbyConnection.getInstance().search(new LabelApplication(), filters);
                List<LabelApplication> forms2 = DerbyConnection.getInstance().search(new LabelApplication(), filters2);
                //forms = forms.stream().filter((e) -> {return e.getLabel().getProductType()==bevType && e.getReceiver().equals(receiver);}).collect(Collectors.toList());

                // sort by date older is first
                forms.sort((LabelApplication o1, LabelApplication o2) -> o1.getApplicationDate().compareTo(o2.getApplicationDate()));
                forms2.sort((LabelApplication o1, LabelApplication o2) -> o1.getApplicationDate().compareTo(o2.getApplicationDate()));

                List<LabelApplication> newforms = new LinkedList<>();

                // collect by oldest -> all from same person until no submissions left or the collected list contains 10 or more elements
                /*while (!forms.isEmpty() && newforms.size()<10)
                {
                    LabelApplication entry = forms.get(0);
                    // collect all entries from the oldest submitter
                    List<LabelApplication> temp = forms.stream().filter((e) -> {return e.getSubmitter().equals(entry.getSubmitter());}).collect(Collectors.toList());
                    newforms.addAll(temp);
                    forms.removeAll(temp);
                }*/
                int i=0;
                for (LabelApplication app : forms2)
                {
                    System.out.println(app);
                    if (i==10)
                        break;
                    newforms.add(app);
                }
                i=0;
                while (newforms.size()<10 && i<forms.size())
                {
                    System.out.println(forms.get(i));
                    newforms.add(forms.get(i));
                    i++;
                }

                for (LabelApplication l : newforms)
                {
                    employee.addApplication(l);
                    l.setStatus(LabelApplication.ApplicationStatus.UNDER_REVIEW);
                    l.setReviewer(employee);
                    DerbyConnection.getInstance().writeEntity(l, l.getPrimaryKeyName());
                }
                DerbyConnection.getInstance().writeEntity(employee, employee.getPrimaryKeyName());
            } catch (SQLException ex) {
                Logger.getLogger(UsEmployeeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static List<UsEmployee> getEmployees(String email)
    {
        List<Filter> filters = new LinkedList<>();;
        filters.add(new RangeFilter(){
            @Override
            public Object getValueMin() {
                return email;
            }

            @Override
            public Object getValueMax() {
                return email+"z";
            }

            @Override
            public String getColumn() {
                return "email";
            }
        });
        filters.add(new ExactFilter(){
            @Override
            public Object getValue() {
                return User.UserType.US_EMPLOYEE.name();
            }

            @Override
            public String getColumn() {
                return "userType";
            }
        });
        UsEmployee e = new UsEmployee();
        List<UsEmployee> employees = null;
        try {
            employees = DerbyConnection.getInstance().search(e, filters, 10, 0);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new LinkedList<>();
        }
        return employees;
    }
}
