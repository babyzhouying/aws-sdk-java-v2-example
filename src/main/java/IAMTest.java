import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;

import java.util.ArrayList;
import java.util.List;

public class IAMTest {
    public static void main(String... arg) {
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();
//        for(Role role:iam.listRoles().roles()){
//            String policyDocument=role.assumeRolePolicyDocument();
//            System.out.println(policyDocument);
//        }

        testListPolicy(iam);




//        try {
//            for (User user : iam.listUsers().users()) {
//                System.out.println(user.userName());
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static void testListPolicy(IamClient iam){
        try {
            String roleName="assume_me";

            List<AttachedPolicy> matchingPolicies = new ArrayList<>();

            boolean done = false;
            String newMarker = null;


            ListAttachedRolePoliciesResponse response;

            if (newMarker == null) {
                ListAttachedRolePoliciesRequest request =
                        ListAttachedRolePoliciesRequest.builder()
                                .roleName(roleName).build();
                response = iam.listAttachedRolePolicies(request);
            } else {
                ListAttachedRolePoliciesRequest request =
                        ListAttachedRolePoliciesRequest.builder()
                                .roleName(roleName)
                                .marker(newMarker).build();
                response = iam.listAttachedRolePolicies(request);
            }

            for (AttachedPolicy policy:response.attachedPolicies()){
                System.out.println(policy.toString());
            }


        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static String createIAMAccessKey(IamClient iam, String user) {

        try {
            CreateAccessKeyRequest request = CreateAccessKeyRequest.builder()
                    .userName(user).build();

            CreateAccessKeyResponse response = iam.createAccessKey(request);
            String keyId = response.accessKey().accessKeyId();
            return keyId;

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
